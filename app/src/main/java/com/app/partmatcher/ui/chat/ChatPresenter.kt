package com.app.partmatcher.ui.chat

import android.util.Log
import com.app.partmatcher.data.api.ApiService
import com.app.partmatcher.data.model.ChatMessageDto
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader

class ChatPresenter(
    private val apiService: ApiService,
    private val token: String,
    private val recipientId: Long,
    private val currentUserId: Long
) : MvpPresenter<ChatView>() {

    private var stompClient: StompClient? = null
    private val gson = Gson()
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        Log.d("ChatPresenter", "Attach: recipientId=$recipientId, userId=$currentUserId, tokenLen=${token.length}")
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
        // Добавляем /websocket в конец URL, так как бэкенд на Spring с SockJS 
        // часто ожидает именно такой путь для чистого WebSocket соединения.
        val baseUrl = "ws://10.180.202.145:8080/ws-support/websocket"
        Log.d("ChatPresenter", "Connecting to Stomp at $baseUrl")
        
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, baseUrl)
        stompClient?.withClientHeartbeat(10000)?.withServerHeartbeat(10000)
        
        val headers = listOf(
            StompHeader("Authorization", "Bearer $token"),
            StompHeader("authToken", "Bearer $token")
        )
        
        val lifecycleDisposable = stompClient?.lifecycle()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.i("ChatPresenter", "Stomp connection opened")
                    LifecycleEvent.Type.CLOSED -> Log.i("ChatPresenter", "Stomp connection closed")
                    LifecycleEvent.Type.ERROR -> {
                        Log.e("ChatPresenter", "Stomp error", lifecycleEvent.exception)
                        viewState.showError("Connection error: ${lifecycleEvent.exception?.message}")
                    }
                    else -> Log.i("ChatPresenter", "Stomp event: ${lifecycleEvent.type}")
                }
            }, { error ->
                Log.e("ChatPresenter", "Lifecycle error", error)
            })
        
        lifecycleDisposable?.let { compositeDisposable.add(it) }

        val topicDisposable = stompClient?.topic("/topic/support")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({ stompMessage ->
                val message = gson.fromJson(stompMessage.payload, ChatMessageDto::class.java)
                Log.d("ChatPresenter", "Message received: ${message.content} from ${message.senderId} to ${message.recipientId}")
                
                // Фильтруем сообщения: показываем только те, что относятся к текущему диалогу
                val isFromMeToHim = message.senderId == currentUserId && message.recipientId == recipientId
                val isFromHimToMe = message.senderId == recipientId && message.recipientId == currentUserId
                
                if (isFromMeToHim || isFromHimToMe) {
                    viewState.onMessageReceived(message)
                }
            }, { error ->
                Log.e("ChatPresenter", "Topic subscription error", error)
            })

        topicDisposable?.let { compositeDisposable.add(it) }
        
        stompClient?.connect(headers)
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        if (stompClient?.isConnected != true) {
            Log.w("ChatPresenter", "Client not connected, trying to send message")
            viewState.showError("Соединение устанавливается...")
        }

        Log.d("ChatPresenter", "Sending message: $content to $recipientId from $currentUserId")
        
        val messageSend = com.app.partmatcher.data.model.ChatMessageSendDto(
            recipientId = recipientId,
            content = content
        )
        val jsonMessage = gson.toJson(messageSend)
        
        val stompMessage = ua.naiksoftware.stomp.dto.StompMessage(
            ua.naiksoftware.stomp.dto.StompCommand.SEND,
            listOf(
                ua.naiksoftware.stomp.dto.StompHeader(ua.naiksoftware.stomp.dto.StompHeader.DESTINATION, "/app/support/message"),
                ua.naiksoftware.stomp.dto.StompHeader(ua.naiksoftware.stomp.dto.StompHeader.CONTENT_TYPE, "application/json")
            ),
            jsonMessage
        )
        
        val sendDisposable = stompClient?.send(stompMessage)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe({
                Log.i("ChatPresenter", "Message sent successfully")
                // Оптимистичное добавление сообщения в список (опционально)
                // viewState.onMessageReceived(ChatMessageDto(senderId = currentUserId, recipientId = recipientId, content = content))
            }, { error ->
                Log.e("ChatPresenter", "Failed to send message", error)
                viewState.showError("Ошибка отправки: ${error.message}")
            })
            
        sendDisposable?.let { compositeDisposable.add(it) }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        stompClient?.disconnect()
        super.onDestroy()
    }
}
