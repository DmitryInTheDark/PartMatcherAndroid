package com.app.partmatcher.ui.chat

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.util.ErrorUtils
import moxy.MvpPresenter

class SupportInboxPresenter(
    private val apiService: ApiService
) : MvpPresenter<SupportInboxView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadInbox()
    }

    private fun loadInbox() {
        viewState.showLoading()
        apiService.getChatContacts().enqueue(object : retrofit2.Callback<List<UserDto>> {
            override fun onResponse(call: retrofit2.Call<List<UserDto>>, response: retrofit2.Response<List<UserDto>>) {
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    viewState.showActiveChats(users)
                    if (users.isEmpty()) viewState.showEmpty() else viewState.showSuccess()
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: retrofit2.Call<List<UserDto>>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }
}
