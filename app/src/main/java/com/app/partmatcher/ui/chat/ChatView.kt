package com.app.partmatcher.ui.chat

import com.app.partmatcher.data.model.ChatMessageDto
import com.app.partmatcher.ui.BaseView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ChatView : BaseView {
    @AddToEndSingle
    fun showChatHistory(history: List<ChatMessageDto>)

    @AddToEndSingle
    fun onMessageReceived(message: ChatMessageDto)
}
