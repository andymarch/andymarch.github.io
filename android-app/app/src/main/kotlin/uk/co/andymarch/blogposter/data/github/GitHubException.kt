package uk.co.andymarch.blogposter.data.github

/** A GitHub REST API call failed with a non-2xx response. */
class GitHubApiException(
    val httpCode: Int,
    override val message: String,
) : Exception(message) {

    val isAuthError: Boolean get() = httpCode == 401
    val isForbidden: Boolean get() = httpCode == 403
    val isNotFound: Boolean get() = httpCode == 404

    companion object {
        fun userMessageFor(httpCode: Int, apiMessage: String?): String = when (httpCode) {
            401 -> "That token was rejected by GitHub. Check it's still valid and hasn't expired."
            403 -> apiMessage?.takeIf { it.isNotBlank() }
                ?: "GitHub refused this request. The token may be missing the Contents permission for this repository."
            404 -> "Not found. Check the repository owner/name are correct and the token can see this repo."
            409 -> "That file changed on GitHub since it was last loaded. Refresh and try again."
            422 -> apiMessage?.takeIf { it.isNotBlank() } ?: "GitHub rejected the request as invalid."
            in 500..599 -> "GitHub is having trouble right now. Try again shortly."
            else -> apiMessage?.takeIf { it.isNotBlank() } ?: "GitHub request failed (HTTP $httpCode)."
        }
    }
}
