package com.app.partmatcher.ui.admin

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface AdminPartListView : BaseView {
    @AddToEndSingle
    fun showParts(parts: List<PartDto>)
}
