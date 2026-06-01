package com.app.partmatcher.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.partmatcher.data.api.NetworkModule
import com.app.partmatcher.data.model.ChatMessageDto
import com.app.partmatcher.databinding.FragmentChatBinding
import com.app.partmatcher.util.TokenManager
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ChatFragment : MvpAppCompatFragment(), ChatView {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val presenter by moxyPresenter {
        val recipientId = arguments?.getLong("recipientId") ?: 0L
        val tokenManager = TokenManager(requireContext())
        ChatPresenter(
            NetworkModule.getApiService(requireContext()),
            tokenManager.getToken() ?: "",
            recipientId,
            tokenManager.getUserId()
        )
    }

    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val currentUserId = TokenManager(requireContext()).getUserId()
        adapter = ChatAdapter(currentUserId = currentUserId)
        
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter

        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text.toString()
            if (content.isNotEmpty()) {
                presenter.sendMessage(content)
                binding.etMessage.text.clear()
            }
        }
        
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    override fun showChatHistory(history: List<ChatMessageDto>) {
        adapter.setMessages(history)
        binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
    }

    override fun onMessageReceived(message: ChatMessageDto) {
        activity?.runOnUiThread {
            adapter.addMessage(message)
            binding.chatRecyclerView.scrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun showLoading() {}

    override fun showSuccess() {}

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showEmpty() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
