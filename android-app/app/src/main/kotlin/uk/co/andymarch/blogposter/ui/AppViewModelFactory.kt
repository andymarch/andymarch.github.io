package uk.co.andymarch.blogposter.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import uk.co.andymarch.blogposter.JekyllPosterApp
import uk.co.andymarch.blogposter.ui.drafts.DraftsViewModel
import uk.co.andymarch.blogposter.ui.editor.EditorViewModel
import uk.co.andymarch.blogposter.ui.login.LoginViewModel
import uk.co.andymarch.blogposter.ui.posts.RecentPostsViewModel
import uk.co.andymarch.blogposter.ui.settings.SettingsViewModel

/**
 * Hand-rolled ViewModel factory, since there's no DI framework wiring constructors
 * for us. [draftId] only matters for [EditorViewModel]; the caller always passes a
 * concrete id — a fresh one for a brand-new post, or an existing draft's id.
 */
class AppViewModelFactory(private val app: JekyllPosterApp, private val draftId: String = "") : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when (modelClass) {
        LoginViewModel::class.java -> LoginViewModel(app.tokenStore, app.settingsStore, app.githubRepository) as T
        EditorViewModel::class.java -> EditorViewModel(
            id = draftId,
            draftStore = app.draftStore,
            settingsStore = app.settingsStore,
            githubRepository = app.githubRepository,
            imageProcessor = app.imageProcessor,
            appContext = app,
        ) as T
        DraftsViewModel::class.java -> DraftsViewModel(app.draftStore) as T
        RecentPostsViewModel::class.java -> RecentPostsViewModel(app.githubRepository, app.settingsStore, app.draftStore) as T
        SettingsViewModel::class.java -> SettingsViewModel(app.settingsStore, app.tokenStore, app.githubRepository) as T
        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
