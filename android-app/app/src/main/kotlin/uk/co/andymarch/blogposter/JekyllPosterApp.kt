package uk.co.andymarch.blogposter

import android.app.Application
import androidx.work.Configuration
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.TokenStore
import uk.co.andymarch.blogposter.data.draft.DraftStore
import uk.co.andymarch.blogposter.data.github.GitHubRepository
import uk.co.andymarch.blogposter.data.github.NetworkModule
import uk.co.andymarch.blogposter.image.ImageProcessor
import uk.co.andymarch.blogposter.publish.PublishNotifications
import uk.co.andymarch.blogposter.publish.PublishWorkerFactory

/**
 * Hand-rolled dependency container — this app is small enough that a DI
 * framework like Hilt would add more boilerplate than it removes.
 */
class JekyllPosterApp : Application(), Configuration.Provider {

    lateinit var tokenStore: TokenStore
        private set
    lateinit var settingsStore: SettingsStore
        private set
    lateinit var draftStore: DraftStore
        private set
    lateinit var githubRepository: GitHubRepository
        private set
    lateinit var imageProcessor: ImageProcessor
        private set

    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
        settingsStore = SettingsStore(this)
        draftStore = DraftStore(this)
        githubRepository = GitHubRepository(
            NetworkModule.createGitHubApi(tokenProvider = { tokenStore.getToken() }, debugLogging = BuildConfig.DEBUG),
        )
        imageProcessor = ImageProcessor(this)
        PublishNotifications.createChannel(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(PublishWorkerFactory(draftStore, settingsStore, githubRepository))
            .build()
}
