package com.app.partmatcher.ui.admin

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.AdminStatisticsDto
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminStatsPresenter(
    private val apiService: ApiService
) : MvpPresenter<AdminStatsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadStatistics()
    }

    private fun loadStatistics() {
        viewState.showLoading()
        apiService.getStatistics().enqueue(object : Callback<AdminStatisticsDto> {
            override fun onResponse(call: Call<AdminStatisticsDto>, response: Response<AdminStatisticsDto>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        viewState.showStatistics(it)
                        viewState.showSuccess()
                    } ?: viewState.showEmpty()
                } else {
                    viewState.showError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<AdminStatisticsDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }
}
