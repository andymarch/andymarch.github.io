package uk.co.andymarch.blogposter.data.github

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserDto(
    val login: String,
    val name: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

@Serializable
data class GitHubRepoDto(
    @SerialName("full_name") val fullName: String,
    @SerialName("default_branch") val defaultBranch: String,
    val permissions: GitHubPermissionsDto? = null,
    val private: Boolean = false,
)

@Serializable
data class GitHubPermissionsDto(
    val push: Boolean = false,
    val admin: Boolean = false,
)

/** A single entry in the Contents API — either a file's content or one row of a directory listing. */
@Serializable
data class GitHubContentDto(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long = 0,
    val type: String,
    val content: String? = null,
    val encoding: String? = null,
    @SerialName("html_url") val htmlUrl: String? = null,
)

@Serializable
data class GitHubCommitIdentity(
    val name: String,
    val email: String,
)

@Serializable
data class PutContentRequest(
    val message: String,
    val content: String,
    val branch: String,
    val sha: String? = null,
    val committer: GitHubCommitIdentity? = null,
    val author: GitHubCommitIdentity? = null,
)

@Serializable
data class DeleteContentRequest(
    val message: String,
    val sha: String,
    val branch: String,
    val committer: GitHubCommitIdentity? = null,
    val author: GitHubCommitIdentity? = null,
)

@Serializable
data class PutContentResponse(
    val content: GitHubContentDto? = null,
    val commit: GitHubCommitDto,
)

@Serializable
data class GitHubCommitDto(
    val sha: String,
    @SerialName("html_url") val htmlUrl: String? = null,
)

@Serializable
data class GitHubApiErrorDto(
    val message: String = "",
    @SerialName("documentation_url") val documentationUrl: String? = null,
)
