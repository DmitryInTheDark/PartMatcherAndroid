package com.app.partmatcher.ui.vin_result

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.data.model.VehicleDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface VinResultView : BaseView {
    @AddToEndSingle
    fun showVehicleInfo(vehicle: VehicleDto)

    @AddToEndSingle
    fun showCompatibleParts(parts: List<PartDto>)

    @OneExecution
    fun navigateToPartDetails(partId: Long)
}
