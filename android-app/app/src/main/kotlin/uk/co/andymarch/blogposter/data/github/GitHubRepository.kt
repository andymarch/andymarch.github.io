package uk.co.andymarch.blogposter.data.github

import java.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

data class RepoAccess(
    val defaultBranch: String,
    val canPush: Boolean,
    val fullName: String,
)

data class PostSummary(
    val name: String,
    val path: String,
    val sha: String,
)

data class CommitResult(
    val path: String,
    val sha: String,
    val commitUrl: String?,
)

data class CommitAuthor(val name: String, val email: String)

/** Repository config the user has to supply once: which repo, and which branch to commit to. */
data class RepoConfig(
    val owner: String,
    val repo: String,
    val branch: String,
)

/**
 * Talks to GitHub's Contents API, which commits directly to a branch server-side —
 * there's no local git checkout, clone, or push involved on the device.
 */
class GitHubRepository(private val api: GitHubApi) {

    private val errorJson = Json { ignoreUnknownKeys = true }

    suspend fun currentUser(): GitHubUserDto = apiCall { api.getAuthenticatedUser() }

    suspend fun verifyRepoAccess(owner: String, repo: String): RepoAccess {
        val dto = apiCall { api.getRepository(owner, repo) }
        return RepoAccess(
            defaultBranch = dto.defaultBranch,
            canPush = dto.permissions?.push ?: false,
            fullName = dto.fullName,
        )
    }

    /** Null means the file doesn't exist at [branch] yet. */
    suspend fun getFileSha(config: RepoConfig, path: String): String? = withContext(Dispatchers.IO) {
        val response = api.getFileContent(config.owner, config.repo, path, config.branch)
        if (response.code() == 404) return@withContext null
        unwrap(response).sha
    }

    suspend fun readFile(config: RepoConfig, path: String): String? = withContext(Dispatchers.IO) {
        val response = api.getFileContent(config.owner, config.repo, path, config.branch)
        if (response.code() == 404) return@withContext null
        val dto = unwrap(response)
        val raw = dto.content ?: return@withContext null
        val cleaned = raw.replace("\n", "")
        String(Base64.getDecoder().decode(cleaned), Charsets.UTF_8)
    }

    /** Creates the file if it doesn't exist, otherwise updates it in place. */
    suspend fun createOrUpdateTextFile(
        config: RepoConfig,
        path: String,
        contents: String,
        commitMessage: String,
        author: CommitAuthor?,
    ): CommitResult = createOrUpdateFile(config, path, contents.toByteArray(Charsets.UTF_8), commitMessage, author)

    suspend fun createOrUpdateFile(
        config: RepoConfig,
        path: String,
        bytes: ByteArray,
        commitMessage: String,
        author: CommitAuthor?,
    ): CommitResult = withContext(Dispatchers.IO) {
        val existingSha = getFileSha(config, path)
        val identity = author?.let { GitHubCommitIdentity(it.name, it.email) }
        val request = PutContentRequest(
            message = commitMessage,
            content = Base64.getEncoder().encodeToString(bytes),
            branch = config.branch,
            sha = existingSha,
            committer = identity,
            author = identity,
        )
        val response = apiCall { api.putFileContent(config.owner, config.repo, path, request) }
        CommitResult(
            path = response.content?.path ?: path,
            sha = response.content?.sha ?: response.commit.sha,
            commitUrl = response.commit.htmlUrl,
        )
    }

    /** No-op if [path] doesn't exist — used to clean up the old file after a renamed republish. */
    suspend fun deleteFile(config: RepoConfig, path: String, commitMessage: String, author: CommitAuthor?) {
        withContext(Dispatchers.IO) {
            val sha = getFileSha(config, path)
            if (sha == null) return@withContext
            val identity = author?.let { GitHubCommitIdentity(it.name, it.email) }
            val request = DeleteContentRequest(
                message = commitMessage,
                sha = sha,
                branch = config.branch,
                committer = identity,
                author = identity,
            )
            apiCall { api.deleteFileContent(config.owner, config.repo, path, request) }
            Unit
        }
    }

    /** Lists posts under `_posts/`, most recent filename first (post filenames are date-prefixed). */
    suspend fun listPosts(config: RepoConfig, directory: String = "_posts"): List<PostSummary> =
        withContext(Dispatchers.IO) {
            val response = api.listDirectory(config.owner, config.repo, directory, config.branch)
            if (response.code() == 404) return@withContext emptyList()
            unwrap(response)
                .filter { it.type == "file" && it.name.endsWith(".md") }
                .sortedByDescending { it.name }
                .map { PostSummary(name = it.name, path = it.path, sha = it.sha) }
        }

    private suspend fun <T> unwrap(response: Response<T>): T {
        if (response.isSuccessful) {
            return response.body() ?: throw GitHubApiException(response.code(), "GitHub returned an empty response.")
        }
        throw toException(response.code(), response.errorBody())
    }

    private suspend fun <T> apiCall(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: HttpException) {
            throw toException(e.code(), e.response()?.errorBody())
        }
    }

    private fun toException(code: Int, errorBody: ResponseBody?): GitHubApiException {
        val apiMessage = errorBody?.string()?.let { body ->
            runCatching { errorJson.decodeFromString(GitHubApiErrorDto.serializer(), body).message }.getOrNull()
        }
        return GitHubApiException(code, GitHubApiException.userMessageFor(code, apiMessage))
    }
}
