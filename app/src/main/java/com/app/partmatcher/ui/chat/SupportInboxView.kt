package com.app.partmatcher.ui.chat

import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface SupportInboxView : BaseView {
    @AddToEndSingle
    fun showActiveChats(users: List<UserDto>)
}
