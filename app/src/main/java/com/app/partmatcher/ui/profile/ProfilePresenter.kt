package com.app.partmatcher.ui.profile

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.util.ErrorUtils
import com.app.partmatcher.util.TokenManager
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilePresenter(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : MvpPresenter<ProfileView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewState.showLoading()
        apiService.getMe().enqueue(object : Callback<UserDto> {
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.showUserInfo(it)
                        viewState.showSuccess()
                    } ?: viewState.showEmpty()
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }

    fun onLogoutClicked() {
        tokenManager.clearToken()
        viewState.navigateToLogin()
    }
}
