package com.app.partmatcher.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.partmatcher.data.model.ChatMessageDto
import com.app.partmatcher.databinding.ItemMessageLeftBinding
import com.app.partmatcher.databinding.ItemMessageRightBinding

class ChatAdapter(private val currentUserId: Long) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessageDto>()

    companion object {
        private const val TYPE_LEFT = 0
        private const val TYPE_RIGHT = 1
    }

    fun setMessages(newMessages: List<ChatMessageDto>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(message: ChatMessageDto) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) TYPE_RIGHT else TYPE_LEFT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_RIGHT) {
            val binding = ItemMessageRightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RightViewHolder(binding)
        } else {
            val binding = ItemMessageLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LeftViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is LeftViewHolder) holder.bind(message)
        else if (holder is RightViewHolder) holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class LeftViewHolder(private val binding: ItemMessageLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageDto) {
            binding.tvMessage.text = message.content
            binding.tvTime.text = message.sentAt ?: ""
        }
    }

    inner class RightViewHolder(private val binding: ItemMessageRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessageDto) {
            binding.tvMessage.text = message.content
            binding.tvTime.text = message.sentAt ?: ""
        }
    }
}
