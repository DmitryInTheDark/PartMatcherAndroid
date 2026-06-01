package com.app.partmatcher.ui.auth

import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.OneExecution

interface RegisterView : BaseView {
    @OneExecution
    fun navigateToHome()
}
