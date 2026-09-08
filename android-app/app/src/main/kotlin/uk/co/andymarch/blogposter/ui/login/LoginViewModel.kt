package uk.co.andymarch.blogposter.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.andymarch.blogposter.data.SettingsStore
import uk.co.andymarch.blogposter.data.TokenStore
import uk.co.andymarch.blogposter.data.github.GitHubRepository

data class LoginUiState(
    val token: String = "",
    val owner: String = "",
    val repo: String = "",
    val isVerifying: Boolean = false,
    val errorMessage: String? = null,
    val isComplete: Boolean = false,
)

class LoginViewModel(
    private val tokenStore: TokenStore,
    private val settingsStore: SettingsStore,
    private val githubRepository: GitHubRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onTokenChanged(value: String) {
        _uiState.value = _uiState.value.copy(token = value, errorMessage = null)
    }

    fun onOwnerChanged(value: String) {
        _uiState.value = _uiState.value.copy(owner = value, errorMessage = null)
    }

    fun onRepoChanged(value: String) {
        _uiState.value = _uiState.value.copy(repo = value, errorMessage = null)
    }

    fun connect() {
        val state = _uiState.value
        val token = state.token.trim()
        val owner = state.owner.trim()
        val repo = state.repo.trim()

        if (token.isEmpty() || owner.isEmpty() || repo.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Fill in a token, repository owner, and repository name.")
            return
        }

        _uiState.value = state.copy(isVerifying = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // Saved before the calls below since the network layer reads the token lazily per-request.
                tokenStore.saveToken(token)
                githubRepository.currentUser()
                val access = githubRepository.verifyRepoAccess(owner, repo)
                if (!access.canPush) {
                    tokenStore.clearToken()
                    _uiState.value = _uiState.value.copy(
                        isVerifying = false,
                        errorMessage = "This token doesn't have write access to $owner/$repo. " +
                            "Check the token's repository access and permissions.",
                    )
                    return@launch
                }
                settingsStore.updateRepo(owner, repo, access.defaultBranch)
                _uiState.value = _uiState.value.copy(isVerifying = false, isComplete = true)
            } catch (e: Exception) {
                tokenStore.clearToken()
                _uiState.value = _uiState.value.copy(isVerifying = false, errorMessage = e.message ?: "Could not connect.")
            }
        }
    }
}
