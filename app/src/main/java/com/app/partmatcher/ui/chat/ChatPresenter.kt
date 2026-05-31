package com.app.partmatcher.ui.chat

import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.ChatMessageDto
import com.google.gson.Gson
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader

class ChatPresenter(
    private val apiService: ApiService,
    private val token: String,
    private val recipientId: Long
) : MvpPresenter<ChatView>() {

    private var stompClient: StompClient? = null
    private val gson = Gson()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadHistory()
        connectWebSocket()
    }

    private fun loadHistory() {
        apiService.getChatHistory(recipientId).enqueue(object : Callback<List<ChatMessageDto>> {
            override fun onResponse(call: Call<List<ChatMessageDto>>, response: Response<List<ChatMessageDto>>) {
                if (response.isSuccessful) {
                    response.body()?.let { viewState.showChatHistory(it) }
                }
            }
            override fun onFailure(call: Call<List<ChatMessageDto>>, t: Throwable) {}
        })
    }

    private fun connectWebSocket() {
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, "ws://localhost:8080/ws-support")
        val headers = listOf(StompHeader("Authorization", "Bearer $token"))
        
        stompClient?.connect(headers)

        stompClient?.topic("/topic/support")?.subscribe { stompMessage ->
            val message = gson.fromJson(stompMessage.payload, ChatMessageDto::class.java)
            viewState.onMessageReceived(message)
        }
    }

    fun sendMessage(content: String) {
        val message = ChatMessageDto(recipientId = recipientId, content = content)
        stompClient?.send("/app/support/message", gson.toJson(message))?.subscribe({
            // Message sent successfully
        }, { error ->
            viewState.showError("Failed to send message: ${error.message}")
        })
    }

    override fun onDestroy() {
        stompClient?.disconnect()
        super.onDestroy()
    }
}
