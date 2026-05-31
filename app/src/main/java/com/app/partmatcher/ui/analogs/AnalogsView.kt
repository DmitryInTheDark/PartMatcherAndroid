package com.app.partmatcher.ui.analogs

import com.app.partmatcher.data.model.AnalogPartDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface AnalogsView : BaseView {
    @AddToEndSingle
    fun showAnalogs(analogs: List<AnalogPartDto>)
}
