package uk.co.andymarch.blogposter.ui.nav

object Routes {
    const val LOGIN = "login"
    const val DRAFTS = "drafts"
    const val POSTS = "posts"
    const val SETTINGS = "settings"
    const val EDITOR_PATTERN = "editor/{draftId}"

    fun editor(draftId: String) = "editor/$draftId"
}
