package com.app.partmatcher.ui.auth

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.AuthRequestDto
import com.app.partmatcher.data.model.AuthResponseDto
import com.app.partmatcher.util.ErrorUtils
import com.app.partmatcher.util.TokenManager
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPresenter(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : MvpPresenter<LoginView>() {

    fun onLoginClicked(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            viewState.showError("Email and password cannot be empty")
            return
        }

        viewState.showLoading()
        val request = AuthRequestDto(email, password)
        apiService.login(request).enqueue(object : Callback<AuthResponseDto> {
            override fun onResponse(call: Call<AuthResponseDto>, response: Response<AuthResponseDto>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.accessToken != null && authResponse.user != null) {
                        tokenManager.saveToken(authResponse.accessToken)
                        tokenManager.saveUser(
                            authResponse.user.id,
                            authResponse.user.name,
                            authResponse.user.roles
                        )
                        viewState.showSuccess()
                        viewState.navigateToHome()
                    } else {
                        viewState.showError("Login failed: invalid response")
                    }
                } else if (response.code() == 401) {
                    viewState.showError("Invalid email or password")
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }

    fun onRegisterClicked() {
        viewState.navigateToRegister()
    }
}
