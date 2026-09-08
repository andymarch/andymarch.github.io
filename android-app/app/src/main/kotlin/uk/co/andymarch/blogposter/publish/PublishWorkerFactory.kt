package uk.co.andymarch.blogposter.publish

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.draft.DraftStore
import uk.co.andymarch.blogposter.data.github.GitHubRepository

/** Hands [PublishWorker] the dependencies it needs, since it isn't built by a DI framework here. */
class PublishWorkerFactory(
    private val draftStore: DraftStore,
    private val settingsStore: SettingsStore,
    private val githubRepository: GitHubRepository,
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ) = when (workerClassName) {
        PublishWorker::class.java.name -> PublishWorker(appContext, workerParameters, draftStore, settingsStore, githubRepository)
        else -> null
    }
}
