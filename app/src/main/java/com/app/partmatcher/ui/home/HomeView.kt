package com.app.partmatcher.ui.home

import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface HomeView : BaseView {
    @AddToEndSingle
    fun showRecentRequests(requests: List<String>)

    @OneExecution
    fun navigateToVinResult(vin: String)

    @OneExecution
    fun navigateToSearchResults(query: String)

    @OneExecution
    fun navigateToFavorites()
}
