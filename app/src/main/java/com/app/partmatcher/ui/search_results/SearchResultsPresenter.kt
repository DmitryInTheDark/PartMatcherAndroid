package com.app.partmatcher.ui.search_results

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.util.ErrorUtils
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchResultsPresenter(
    private val apiService: ApiService,
    private val query: String
) : MvpPresenter<SearchResultsView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        search()
    }

    private fun search() {
        viewState.showLoading()
        apiService.searchParts(query).enqueue(object : Callback<List<PartDto>> {
            override fun onResponse(call: Call<List<PartDto>>, response: Response<List<PartDto>>) {
                if (response.isSuccessful) {
                    val results = response.body() ?: emptyList()
                    viewState.showResults(results)
                    if (results.isEmpty()) viewState.showEmpty() else viewState.showSuccess()
                } else if (response.code() == 401) {
                    viewState.onUnauthorized()
                } else {
                    viewState.showError(ErrorUtils.parseError(response))
                }
            }

            override fun onFailure(call: Call<List<PartDto>>, t: Throwable) {
                viewState.showError(t.message ?: "Network error")
            }
        })
    }

    fun onPartClicked(partId: Long) {
        viewState.navigateToPartDetails(partId)
    }
}
