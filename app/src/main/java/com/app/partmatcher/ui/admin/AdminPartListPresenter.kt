package com.app.partmatcher.ui.admin

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.ApiResponseDto
import com.app.partmatcher.data.model.PartDto
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminPartListPresenter(
    private val apiService: ApiService
) : MvpPresenter<AdminPartListView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadParts("")
    }

    fun onSearchQueryChanged(query: String) {
        loadParts(query)
    }

    private fun loadParts(query: String) {
        viewState.showLoading()
        apiService.searchParts(query).enqueue(object : Callback<List<PartDto>> {
            override fun onResponse(call: Call<List<PartDto>>, response: Response<List<PartDto>>) {
                if (response.isSuccessful) {
                    val parts = response.body() ?: emptyList()
                    viewState.showParts(parts)
                    if (parts.isEmpty()) viewState.showEmpty() else viewState.showSuccess()
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<PartDto>>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }

    fun onDeletePartClicked(partId: Long) {
        apiService.deletePart(partId).enqueue(object : Callback<ApiResponseDto> {
            override fun onResponse(call: Call<ApiResponseDto>, response: Response<ApiResponseDto>) {
                if (response.isSuccessful) {
                    loadParts("") // Refresh list
                } else {
                    viewState.showError("Delete failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponseDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }
}
