package com.app.partmatcher.ui

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface BaseView : MvpView {
    @AddToEndSingle
    fun showLoading()

    @AddToEndSingle
    fun showSuccess()

    @AddToEndSingle
    fun showError(message: String)

    @AddToEndSingle
    fun showEmpty()

    @moxy.viewstate.strategy.alias.OneExecution
    fun onUnauthorized() {}
}
