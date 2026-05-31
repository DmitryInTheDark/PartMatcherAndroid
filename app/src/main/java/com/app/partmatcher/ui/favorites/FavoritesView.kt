package com.app.partmatcher.ui.favorites

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.alias.AddToEndSingle

interface FavoritesView : BaseView {
    @AddToEndSingle
    fun showFavorites(parts: List<PartDto>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun navigateToPartDetails(partId: Long)
}
