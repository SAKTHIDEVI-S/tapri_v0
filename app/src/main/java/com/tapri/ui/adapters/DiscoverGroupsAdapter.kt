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

class DiscoverGroupsAdapter(
    private var groups: List<GroupDto> = emptyList(),
    private val onJoinClick: (GroupDto) -> Unit
) : RecyclerView.Adapter<DiscoverGroupsAdapter.DiscoverGroupViewHolder>() {

    class DiscoverGroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupAvatar: ImageView = view.findViewById(R.id.groupAvatar)
        val groupName: TextView = view.findViewById(R.id.groupName)
        val memberCount: TextView = view.findViewById(R.id.memberCount)
        val category: TextView = view.findViewById(R.id.category)
        val groupDescription: TextView = view.findViewById(R.id.groupDescription)
        val joinButton: TextView = view.findViewById(R.id.joinButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscoverGroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discover_group, parent, false)
        return DiscoverGroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscoverGroupViewHolder, position: Int) {
        val group = groups[position]
        
        holder.groupName.text = group.name ?: "Unknown Group"
        holder.memberCount.text = formatMemberCount(group.membersCount ?: 0)
        holder.category.text = group.category ?: "General"
        holder.groupDescription.text = group.description ?: "Join this group to connect with like-minded people"
        
        // Update join button based on membership status
        if (group.isJoined == true) {
            holder.joinButton.text = "Joined"
            holder.joinButton.background = holder.itemView.context.getDrawable(R.drawable.card_stroke_background)
            holder.joinButton.setTextColor(holder.itemView.context.getColor(R.color.primary_red))
            holder.joinButton.isEnabled = false
        } else {
            holder.joinButton.text = "Join"
            holder.joinButton.background = holder.itemView.context.getDrawable(R.drawable.primary_button_background)
            holder.joinButton.setTextColor(holder.itemView.context.getColor(R.color.white))
            holder.joinButton.isEnabled = true
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
        
        holder.joinButton.setOnClickListener {
            if (group.isJoined != true) {
                onJoinClick(group)
            }
        }
    }

    override fun getItemCount() = groups.size

    fun updateGroups(newGroups: List<GroupDto>) {
        groups = newGroups
        notifyDataSetChanged()
    }
    
    fun getGroups(): List<GroupDto> = groups
    
    private fun formatMemberCount(count: Int): String {
        return when {
            count >= 1000000 -> "${count / 1000000}M members"
            count >= 1000 -> "${count / 1000}K members"
            else -> "$count members"
        }
    }
}
