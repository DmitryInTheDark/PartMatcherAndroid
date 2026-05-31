package com.app.partmatcher.ui.vin_result

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.VehicleSearchResultDto
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VinResultPresenter(
    private val apiService: ApiService,
    private val vin: String
) : MvpPresenter<VinResultView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadVinResults()
    }

    private fun loadVinResults() {
        viewState.showLoading()
        apiService.searchByVin(vin).enqueue(object : Callback<VehicleSearchResultDto> {
            override fun onResponse(
                call: Call<VehicleSearchResultDto>,
                response: Response<VehicleSearchResultDto>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        viewState.showVehicleInfo(result.vehicle)
                        if (result.compatibleParts.isEmpty()) {
                            viewState.showEmpty()
                        } else {
                            viewState.showCompatibleParts(result.compatibleParts)
                            viewState.showSuccess()
                        }
                    } else {
                        viewState.showEmpty()
                    }
                } else {
                    viewState.showError("Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<VehicleSearchResultDto>, t: Throwable) {
                viewState.showError(t.message ?: "Unknown error")
            }
        })
    }

    fun onPartClicked(partId: Long) {
        viewState.navigateToPartDetails(partId)
    }
}
