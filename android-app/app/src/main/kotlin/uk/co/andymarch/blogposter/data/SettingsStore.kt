package uk.co.andymarch.blogposter.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import uk.co.andymarch.blogposter.data.github.CommitAuthor
import uk.co.andymarch.blogposter.data.github.RepoConfig

private val Context.dataStore by preferencesDataStore(name = "settings")

data class BlogSettings(
    val owner: String = "",
    val repo: String = "",
    val branch: String = "master",
    val authorName: String = "",
    val authorEmail: String = "",
    val imageMaxDimensionPx: Int = 2048,
    val imageJpegQuality: Int = 85,
) {
    val isRepoConfigured: Boolean get() = owner.isNotBlank() && repo.isNotBlank() && branch.isNotBlank()

    fun toRepoConfig(): RepoConfig? =
        if (isRepoConfigured) RepoConfig(owner = owner, repo = repo, branch = branch) else null

    fun toCommitAuthor(): CommitAuthor? =
        if (authorName.isNotBlank() && authorEmail.isNotBlank()) CommitAuthor(authorName, authorEmail) else null
}

class SettingsStore(private val context: Context) {

    val settings: Flow<BlogSettings> = context.dataStore.data.map { prefs ->
        BlogSettings(
            owner = prefs[Keys.OWNER] ?: "",
            repo = prefs[Keys.REPO] ?: "",
            branch = prefs[Keys.BRANCH] ?: "master",
            authorName = prefs[Keys.AUTHOR_NAME] ?: "",
            authorEmail = prefs[Keys.AUTHOR_EMAIL] ?: "",
            imageMaxDimensionPx = prefs[Keys.IMAGE_MAX_DIMENSION] ?: 2048,
            imageJpegQuality = prefs[Keys.IMAGE_JPEG_QUALITY] ?: 85,
        )
    }

    suspend fun current(): BlogSettings = settings.first()

    suspend fun updateRepo(owner: String, repo: String, branch: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.OWNER] = owner.trim()
            prefs[Keys.REPO] = repo.trim()
            prefs[Keys.BRANCH] = branch.trim()
        }
    }

    suspend fun updateAuthor(name: String, email: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AUTHOR_NAME] = name.trim()
            prefs[Keys.AUTHOR_EMAIL] = email.trim()
        }
    }

    suspend fun updateImageOptions(maxDimensionPx: Int, jpegQuality: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IMAGE_MAX_DIMENSION] = maxDimensionPx
            prefs[Keys.IMAGE_JPEG_QUALITY] = jpegQuality
        }
    }

    private object Keys {
        val OWNER = stringPreferencesKey("owner")
        val REPO = stringPreferencesKey("repo")
        val BRANCH = stringPreferencesKey("branch")
        val AUTHOR_NAME = stringPreferencesKey("author_name")
        val AUTHOR_EMAIL = stringPreferencesKey("author_email")
        val IMAGE_MAX_DIMENSION = intPreferencesKey("image_max_dimension")
        val IMAGE_JPEG_QUALITY = intPreferencesKey("image_jpeg_quality")
    }
}
