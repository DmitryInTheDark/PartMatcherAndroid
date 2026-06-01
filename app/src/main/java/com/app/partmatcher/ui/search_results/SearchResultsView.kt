package com.app.partmatcher.ui.search_results

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface SearchResultsView : BaseView {
    @AddToEndSingle
    fun showResults(parts: List<PartDto>)

    @OneExecution
    fun navigateToPartDetails(partId: Long)
}
