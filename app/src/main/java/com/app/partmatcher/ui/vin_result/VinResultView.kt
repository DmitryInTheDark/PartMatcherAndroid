package com.app.partmatcher.ui.vin_result

import com.app.partmatcher.data.model.PartDto
import com.app.partmatcher.data.model.VehicleDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface VinResultView : BaseView {
    @AddToEndSingle
    fun showVehicleInfo(vehicle: VehicleDto)

    @AddToEndSingle
    fun showCompatibleParts(parts: List<PartDto>)

    @AddToEndSingle
    fun navigateToPartDetails(partId: Long)
}
