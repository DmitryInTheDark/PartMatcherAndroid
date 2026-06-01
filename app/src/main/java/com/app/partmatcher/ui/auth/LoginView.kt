package com.app.partmatcher.ui.auth

import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.OneExecution

interface LoginView : BaseView {
    @OneExecution
    fun navigateToHome()

    @OneExecution
    fun navigateToRegister()
}
