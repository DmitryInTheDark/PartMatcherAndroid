package com.app.partmatcher.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.partmatcher.data.model.UserDto
import com.app.partmatcher.databinding.ItemChatUserBinding

class UserInboxAdapter(
    private val onUserClick: (UserDto) -> Unit
) : RecyclerView.Adapter<UserInboxAdapter.UserViewHolder>() {

    private var users: List<UserDto> = emptyList()

    fun setUsers(newUsers: List<UserDto>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemChatUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    inner class UserViewHolder(private val binding: ItemChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserDto) {
            binding.tvUserName.text = user.name
            binding.tvLastMessage.text = "Нажмите, чтобы открыть чат"
            binding.root.setOnClickListener { onUserClick(user) }
        }
    }
}
