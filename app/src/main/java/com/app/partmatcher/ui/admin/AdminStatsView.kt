package com.app.partmatcher.ui.admin

import com.app.partmatcher.data.model.AdminStatisticsDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface AdminStatsView : BaseView {
    @AddToEndSingle
    fun showStatistics(stats: AdminStatisticsDto)
}
