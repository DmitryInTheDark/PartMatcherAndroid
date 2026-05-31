package com.app.partmatcher.ui.analogs

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.AnalogPartDto
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnalogsPresenter(
    private val apiService: ApiService,
    private val partId: Long
) : MvpPresenter<AnalogsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadAnalogs()
    }

    private fun loadAnalogs() {
        viewState.showLoading()
        apiService.getAnalogs(partId).enqueue(object : Callback<List<AnalogPartDto>> {
            override fun onResponse(call: Call<List<AnalogPartDto>>, response: Response<List<AnalogPartDto>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body.isNullOrEmpty()) {
                        viewState.showEmpty()
                    } else {
                        viewState.showAnalogs(body)
                        viewState.showSuccess()
                    }
                } else {
                    viewState.showError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<AnalogPartDto>>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }
}
