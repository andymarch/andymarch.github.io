package uk.co.andymarch.blogposter.publish

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.LocalDate
import uk.co.andymarch.blogposter.core.FrontMatter
import uk.co.andymarch.blogposter.core.PostDocument
import uk.co.andymarch.blogposter.core.PostPath
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.draft.DraftStatus
import uk.co.andymarch.blogposter.data.draft.DraftStore
import uk.co.andymarch.blogposter.data.github.GitHubApiException
import uk.co.andymarch.blogposter.data.github.GitHubRepository

/**
 * Writes a draft's post file to `_posts/` on GitHub. Runs as a WorkManager job
 * (rather than a plain coroutine tied to a ViewModel) so tapping Publish and
 * then switching away from the app doesn't abandon the commit.
 */
class PublishWorker(
    context: Context,
    params: WorkerParameters,
    private val draftStore: DraftStore,
    private val settingsStore: SettingsStore,
    private val githubRepository: GitHubRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val draftId = inputData.getString(KEY_DRAFT_ID) ?: return Result.failure()
        val draft = draftStore.get(draftId) ?: return Result.failure()

        draftStore.upsert(draft.copy(status = DraftStatus.PUBLISHING, lastError = null))

        return try {
            val settings = settingsStore.current()
            val repoConfig = settings.toRepoConfig()
                ?: error("Set up the repository in Settings before publishing.")

            val date = LocalDate.parse(draft.dateIso)
            val document = PostDocument(
                frontMatter = FrontMatter(
                    type = draft.typeValue,
                    title = draft.title,
                    date = date,
                    tags = draft.tags,
                ),
                body = draft.body,
            )
            val path = PostPath.postFilePath(date, draft.title)

            val commit = githubRepository.createOrUpdateTextFile(
                config = repoConfig,
                path = path,
                contents = document.render(),
                commitMessage = "Publish \"${draft.title}\"",
                author = settings.toCommitAuthor(),
            )

            val previousPath = draft.publishedPostPath
            if (previousPath != null && previousPath != commit.path) {
                // The title/date changed since this post was last published, so it now lives at a
                // different path — clean up the old file rather than leaving a stale duplicate.
                runCatching {
                    githubRepository.deleteFile(
                        config = repoConfig,
                        path = previousPath,
                        commitMessage = "Remove \"${draft.title}\" from its old location",
                        author = settings.toCommitAuthor(),
                    )
                }
            }

            draftStore.upsert(
                draft.copy(status = DraftStatus.PUBLISHED, publishedPostPath = commit.path, lastError = null),
            )
            PublishNotifications.success(applicationContext, draft.title)
            Result.success()
        } catch (e: Exception) {
            val message = (e as? GitHubApiException)?.message ?: e.message ?: "Unknown error"
            draftStore.upsert(draft.copy(status = DraftStatus.FAILED, lastError = message))
            PublishNotifications.failure(applicationContext, draft.title, message)
            Result.failure(workDataOf(KEY_ERROR to message))
        }
    }

    companion object {
        const val KEY_DRAFT_ID = "draft_id"
        const val KEY_ERROR = "error"

        fun uniqueWorkName(draftId: String) = "publish-$draftId"
    }
}
