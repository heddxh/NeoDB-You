package day.vitayuzu.neodb.ui.page.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import day.vitayuzu.neodb.data.AppSettingsManager
import day.vitayuzu.neodb.data.AppSettingsManager.Companion.ONBOARDING_COMPLETED
import day.vitayuzu.neodb.data.AuthRepository
import day.vitayuzu.neodb.data.OtherRepository
import day.vitayuzu.neodb.data.schema.PublicInstanceSchema
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val otherRepo: OtherRepository,
    private val authRepo: AuthRepository,
    private val appSettingsManager: AppSettingsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchPublicInstances()
    }

    fun fetchPublicInstances() {
        otherRepo.fetchPublicInstancesFlow
            .onEach {
                val sorted = it.sortedByDescending { instance -> instance.totalUsers }
                _uiState.update { it.copy(publicInstances = sorted) }
            }.onStart {
                _uiState.update { it.copy(isLoadingInstances = true) }
            }.onCompletion {
                _uiState.update { it.copy(isLoadingInstances = false) }
            }.launchIn(viewModelScope)
    }

    fun fetchInstanceInfo(instanceUrl: String) {
        _uiState.update { it.copy(isFetchingInstanceInfo = true) }
        viewModelScope.launch {
            val normalizedUrl = instanceUrl.toNormalizedUrl()
            authRepo
                .validateInstanceUrl(normalizedUrl)
                .onSuccess { instanceInfo ->
                    _uiState.update {
                        it.copy(
                            isFetchingInstanceInfo = false,
                            selectedInstanceUrl = instanceInfo.uri,
                            selectedInstanceName = instanceInfo.title,
                            selectedInstanceVersion = instanceInfo.version,
                            selectedInstanceUserCount = instanceInfo.stats.userCount,
                        )
                    }
                }.onFailure {
                    Log.e("OnboardingViewModel", "Failed to fetch instance info", it)
                    _uiState.update { state -> state.copy(isFetchingInstanceInfo = false) }
                }
        }
    }

    fun selectInstance(instance: PublicInstanceSchema) {
        _uiState.update {
            it.copy(
                selectedInstanceUrl = instance.domain,
                selectedInstanceName = instance.title,
                selectedInstanceVersion = instance.version,
                selectedInstanceUserCount = instance.totalUsers,
            )
        }
    }

    fun clearSelectedInstance() {
        _uiState.update {
            it.copy(
                selectedInstanceUrl = "",
                selectedInstanceName = "",
                selectedInstanceVersion = "",
                selectedInstanceUserCount = 0,
            )
        }
    }

    /**
     * Register client and return client id as flow.
     */
    fun getClientId() = flow {
        _uiState.update { it.copy(isPreparingOauth = true) }
        authRepo.registerClientIfNeeded(uiState.value.selectedInstanceUrl).onSuccess {
            emit(it.first)
        }
    }.onCompletion { _uiState.update { it.copy(isPreparingOauth = false) } }

    fun handleAuthCode(code: String) = flow<Unit> {
        // Called from OauthActivity.
        // Since `OauthActivity` creates a new instance of this ViewModel,
        // the instance URL needs to be retrieved from the AuthRepository rather than the UI state.
        with(authRepo) {
            val instanceUrl = accountStatus.value.instanceUrl
            _uiState.update { it.copy(isExchangingAccessToken = true) }
            registerClientIfNeeded(instanceUrl).onSuccess { (clientId, clientSecret) ->
                exchangeAccessToken(clientId, clientSecret, code)
            }
        }
    }.onCompletion {
        _uiState.update { it.copy(isExchangingAccessToken = false) }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            appSettingsManager.store(ONBOARDING_COMPLETED, true)
        }
    }

    fun goToNextPage() {
        _uiState.update { it.copy(currentPage = it.currentPage + 1) }
    }

    fun goToPreviousPage() {
        _uiState.update { it.copy(currentPage = maxOf(0, it.currentPage - 1)) }
    }

    fun resetOauthState() {
        _uiState.update { it.copy(isPreparingOauth = false) }
    }

    private companion object {
        val schemeRegex by lazy { Regex("^https?://") }
        val trailingSlashRegex by lazy { Regex("/$") }

        /**
         * Remove scheme and trailing slash from url string.
         */
        fun String.toNormalizedUrl(): String =
            this.replaceFirst(schemeRegex, "").replaceFirst(trailingSlashRegex, "")
    }
}

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isLoadingInstances: Boolean = true,
    val publicInstances: List<PublicInstanceSchema> = emptyList(),
    val isFetchingInstanceInfo: Boolean = false,
    val selectedInstanceUrl: String = "",
    val selectedInstanceName: String = "",
    val selectedInstanceVersion: String = "",
    val selectedInstanceUserCount: Int = 0,
    val isPreparingOauth: Boolean = false,
    val isExchangingAccessToken: Boolean = false,
)
