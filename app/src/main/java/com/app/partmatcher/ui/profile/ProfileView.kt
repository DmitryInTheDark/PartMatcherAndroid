package com.app.partmatcher.ui.profile

import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface ProfileView : BaseView {
    @AddToEndSingle
    fun showUserInfo(user: UserDto)

    @OneExecution
    fun navigateToLogin()
}
