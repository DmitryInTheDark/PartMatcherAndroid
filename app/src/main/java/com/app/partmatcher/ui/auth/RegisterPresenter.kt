package com.app.partmatcher.ui.auth

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.AuthResponseDto
import com.app.partmatcher.data.model.UserRegistrationDto
import com.app.partmatcher.util.ErrorUtils
import com.app.partmatcher.util.TokenManager
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterPresenter(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : MvpPresenter<RegisterView>() {

    fun onRegisterClicked(name: String, email: String, password: String) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            viewState.showError("All fields are required")
            return
        }

        viewState.showLoading()
        val request = UserRegistrationDto(name, email, password)
        apiService.register(request).enqueue(object : Callback<AuthResponseDto> {
            override fun onResponse(call: Call<AuthResponseDto>, response: Response<AuthResponseDto>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse?.accessToken != null) {
                        tokenManager.saveToken(authResponse.accessToken)
                        viewState.showSuccess()
                        viewState.navigateToHome()
                    } else {
                        viewState.showError("Registration failed: empty token")
                    }
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<AuthResponseDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }
}
