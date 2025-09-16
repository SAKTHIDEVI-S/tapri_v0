package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.widget.ImageView
import com.tapri.R
import com.tapri.network.GroupDto

class MyGroupsAdapter(
    private var groups: List<GroupDto> = emptyList(),
    private val onGroupClick: (GroupDto) -> Unit
) : RecyclerView.Adapter<MyGroupsAdapter.MyGroupViewHolder>() {

    class MyGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupAvatar: ImageView = view.findViewById(R.id.groupAvatar)
        val groupName: TextView = view.findViewById(R.id.groupName)
        val lastMessage: TextView = view.findViewById(R.id.lastMessage)
        val lastMessageTime: TextView = view.findViewById(R.id.lastMessageTime)
        val unreadBadge: TextView = view.findViewById(R.id.unreadBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_group, parent, false)
        return MyGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyGroupViewHolder, position: Int) {
        val group = groups[position]
        
        holder.groupName.text = group.name ?: "Unknown Group"
        holder.lastMessage.text = group.lastMessage ?: "No messages yet"
        holder.lastMessageTime.text = formatTime(group.lastMessageTime)
        
        // Set unread badge
        val unreadCount = group.unreadCount ?: 0
        if (unreadCount > 0) {
            holder.unreadBadge.text = if (unreadCount > 99) "99+" else unreadCount.toString()
            holder.unreadBadge.visibility = View.VISIBLE
        } else {
            holder.unreadBadge.visibility = View.GONE
        }
        
        // Load group avatar
        if (!group.avatarUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(group.avatarUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(holder.groupAvatar)
        } else {
            holder.groupAvatar.setImageResource(R.drawable.ic_profile)
        }
        
        holder.itemView.setOnClickListener {
            onGroupClick(group)
        }
    }

    override fun getItemCount() = groups.size

    fun updateGroups(newGroups: List<GroupDto>) {
        groups = newGroups
        notifyDataSetChanged()
    }
    
    fun getGroups(): List<GroupDto> = groups
    
    private fun formatTime(timestamp: String?): String {
        if (timestamp.isNullOrEmpty()) return "Now"
        
        return try {
            // Simple formatting for demo - in real app, use proper date formatting
            "2:30 PM"
        } catch (e: Exception) {
            "Now"
        }
    }
}
