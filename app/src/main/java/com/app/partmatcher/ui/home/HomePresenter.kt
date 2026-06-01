package com.app.partmatcher.ui.home

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.util.TokenManager
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePresenter(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : MvpPresenter<HomeView>() {

    private var suggestionsCall: Call<List<PartDto>>? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadRecentRequests()
        checkSupportRoleAndLoadInfo()
    }

    private fun checkSupportRoleAndLoadInfo() {
        val roles = tokenManager.getRoles()
        if (roles.contains("SUPPORT") || roles.contains("ROLE_SUPPORT")) {
            val name = tokenManager.getUserName() ?: "Сотрудник"
            
            // Show greeting immediately with 0/loading state
            viewState.showSupportGreeting(name, 0)
            
            apiService.getChatContacts().enqueue(object : Callback<List<UserDto>> {
                override fun onResponse(call: Call<List<UserDto>>, response: Response<List<UserDto>>) {
                    if (response.isSuccessful) {
                        val chatCount = response.body()?.size ?: 0
                        viewState.showSupportGreeting(name, chatCount)
                    } else {
                        // Keep the greeting but maybe log error
                        android.util.Log.e("HomePresenter", "Failed to load chats: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<UserDto>>, t: Throwable) {
                    android.util.Log.e("HomePresenter", "Chat contacts API error", t)
                }
            })
        }
    }

    private fun loadRecentRequests() {
        // Mocking recent requests as per UI Spec
        val mockRequests = listOf("BMW X5", "Масло 5W-30", "BOSCH Filter", "JT123456789012345")
        viewState.showRecentRequests(mockRequests)
    }

    fun onSearchQueryChanged(query: String) {
        if (query.length < 2) {
            viewState.showSearchSuggestions(emptyList())
            return
        }

        suggestionsCall?.cancel()
        suggestionsCall = apiService.searchParts(query)
        suggestionsCall?.enqueue(object : Callback<List<PartDto>> {
            override fun onResponse(call: Call<List<PartDto>>, response: Response<List<PartDto>>) {
                if (response.isSuccessful) {
                    val suggestions = response.body()?.map { "${it.name} (${it.article})" } ?: emptyList()
                    viewState.showSearchSuggestions(suggestions)
                }
            }

            override fun onFailure(call: Call<List<PartDto>>, t: Throwable) {
                // Ignore errors for suggestions
            }
        })
    }

    fun onVinSearchClicked(vin: String) {
        if (vin.length == 17) {
            viewState.navigateToVinResult(vin)
        } else {
            viewState.showError("VIN must be 17 characters long")
        }
    }

    fun onTextSearchClicked(query: String) {
        if (query.isNotEmpty()) {
            viewState.navigateToSearchResults(query)
        }
    }

    fun onFavoritesClicked() {
        viewState.navigateToFavorites()
    }

    override fun onDestroy() {
        suggestionsCall?.cancel()
        super.onDestroy()
    }
}
