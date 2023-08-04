package com.example.chamberlyapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chamberlyapp.databinding.MessageListItemBinding

class ChatAdapter( private val currentUserUid: String) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private val messages = mutableListOf <ChatMessage>()
    class ChatViewHolder(private val binding: MessageListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: ChatMessage, currentUserUid: String) {
            binding.messageTextView.text = message.content

            val layoutParams = binding.cardGchatMessageMe.layoutParams as ConstraintLayout.LayoutParams
            if (message.senderUid == currentUserUid) {
                layoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET
                layoutParams.endToEnd = binding.messageTextView.id
            } else {
                layoutParams.startToStart = binding.cardGchatMessageMe.id
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET
            }
            binding.cardGchatMessageMe.layoutParams = layoutParams
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = MessageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message, currentUserUid)
    }

    override fun getItemCount(): Int {
        return messages.size
    }
    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }
}
