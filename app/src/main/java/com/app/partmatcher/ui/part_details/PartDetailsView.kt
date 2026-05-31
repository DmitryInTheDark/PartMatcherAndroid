package com.app.partmatcher.ui.part_details

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface PartDetailsView : BaseView {
    @AddToEndSingle
    fun showPartDetails(part: PartDto)

    @AddToEndSingle
    fun showFavoriteStatus(isFavorite: Boolean)

    @OneExecution
    fun navigateToAnalogs(partId: Long)

    @OneExecution
    fun navigateToFavorites()
}
