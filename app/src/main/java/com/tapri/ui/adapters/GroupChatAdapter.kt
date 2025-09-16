package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.network.GroupMessageDto
import com.tapri.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class GroupChatAdapter(
    private var messages: MutableList<GroupMessageDto> = mutableListOf(),
    private val sessionManager: SessionManager? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_OWN_MESSAGE = 1
        private const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    class OwnMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    class OtherMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val senderName: TextView = view.findViewById(R.id.senderName)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val timestamp: TextView = view.findViewById(R.id.timestamp)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val currentUserId = sessionManager?.getUserSession()?.id
        val isOwnMessage = message.user?.id == currentUserId
        
        // Debug logging
        android.util.Log.d("GroupChatAdapter", "Message ${position}: user=${message.user?.id}, currentUser=$currentUserId, isOwn=$isOwnMessage")
        
        return if (isOwnMessage) VIEW_TYPE_OWN_MESSAGE else VIEW_TYPE_OTHER_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_OWN_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_own_message, parent, false)
                OwnMessageViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_other_message, parent, false)
                OtherMessageViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        
        when (holder) {
            is OwnMessageViewHolder -> {
                holder.messageText.text = message.content ?: ""
                holder.timestamp.text = formatTimestamp(message.createdAt)
            }
            is OtherMessageViewHolder -> {
                holder.senderName.text = message.user?.name ?: "Unknown"
                holder.messageText.text = message.content ?: ""
                holder.timestamp.text = formatTimestamp(message.createdAt)
            }
        }
    }

    override fun getItemCount() = messages.size

    fun addMessage(message: GroupMessageDto) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun updateMessages(newMessages: List<GroupMessageDto>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
    
    private fun formatTimestamp(timestamp: String?): String {
        if (timestamp.isNullOrEmpty()) return "Now"
        
        return try {
            // Parse ISO timestamp and format it
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = sdf.parse(timestamp)
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            timeFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Now"
        }
    }
}
