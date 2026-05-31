package com.app.partmatcher.ui.home

import moxy.MvpPresenter

class HomePresenter : MvpPresenter<HomeView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadRecentRequests()
    }

    private fun loadRecentRequests() {
        // Mocking recent requests as per UI Spec
        val mockRequests = listOf("BMW X5", "Масло 5W-30", "BOSCH Filter", "JT123456789012345")
        viewState.showRecentRequests(mockRequests)
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
}
