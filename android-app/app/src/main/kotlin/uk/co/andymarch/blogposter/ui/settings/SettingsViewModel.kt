package uk.co.andymarch.blogposter.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.BlogSettings
import uk.co.andymarch.blogposter.data.TokenStore
import uk.co.andymarch.blogposter.data.github.GitHubRepository

class SettingsViewModel(
    private val settingsStore: SettingsStore,
    private val tokenStore: TokenStore,
    private val githubRepository: GitHubRepository,
) : ViewModel() {

    val settings: StateFlow<BlogSettings> = settingsStore.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        BlogSettings(),
    )

    fun updateRepo(owner: String, repo: String, branch: String) {
        viewModelScope.launch { settingsStore.updateRepo(owner, repo, branch) }
    }

    fun updateAuthor(name: String, email: String) {
        viewModelScope.launch { settingsStore.updateAuthor(name, email) }
    }

    fun updateImageOptions(maxDimensionPx: Int, jpegQuality: Int) {
        viewModelScope.launch { settingsStore.updateImageOptions(maxDimensionPx, jpegQuality) }
    }

    fun signOut() {
        tokenStore.clearToken()
    }

    private val _verifyResult = MutableStateFlow<String?>(null)
    val verifyResult: StateFlow<String?> = _verifyResult

    fun verifyAccess(owner: String, repo: String) {
        viewModelScope.launch {
            _verifyResult.value = null
            _verifyResult.value = try {
                val access = githubRepository.verifyRepoAccess(owner.trim(), repo.trim())
                if (access.canPush) {
                    "Connected to ${access.fullName} (default branch: ${access.defaultBranch})"
                } else {
                    "Token can see ${access.fullName} but doesn't have write access to it."
                }
            } catch (e: Exception) {
                e.message ?: "Could not verify access."
            }
        }
    }
}
