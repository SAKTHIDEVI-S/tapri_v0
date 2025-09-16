package com.tapri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tapri.R
import com.tapri.network.GroupDto
import com.tapri.utils.TimeUtils

class GroupsAdapter(
    private val context: Context,
    private var groups: List<GroupDto>,
    private val onGroupClick: (GroupDto) -> Unit
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int = groups.size

    fun updateGroups(newGroups: List<GroupDto>) {
        groups = newGroups
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val groupAvatar: ImageView = itemView.findViewById(R.id.groupAvatar)
        private val groupName: TextView = itemView.findViewById(R.id.groupName)
        private val groupDescription: TextView = itemView.findViewById(R.id.groupDescription)
        private val memberCount: TextView = itemView.findViewById(R.id.memberCount)
        private val lastActive: TextView = itemView.findViewById(R.id.lastActive)
        private val joinButton: TextView = itemView.findViewById(R.id.joinButton)

        fun bind(group: GroupDto) {
            groupName.text = group.name
            groupDescription.text = group.description ?: "No description available"
            memberCount.text = "${group.membersCount} members"
            
            // Format last active time
            try {
                lastActive.text = if (!group.createdAt.isNullOrEmpty()) {
                    TimeUtils.getRelativeTime(group.createdAt)
                } else {
                    "Recently active"
                }
            } catch (e: Exception) {
                lastActive.text = "Recently active"
            }

            // Load group avatar
            Glide.with(context)
                .load(group.photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_group)
                .error(R.drawable.ic_group)
                .into(groupAvatar)

            // Set join button state
            if (group.isJoined == true) {
                joinButton.text = "Joined"
                joinButton.setBackgroundResource(R.drawable.secondary_button_background)
                joinButton.setTextColor(context.resources.getColor(android.R.color.black, null))
            } else {
                joinButton.text = "Join"
                joinButton.setBackgroundResource(R.drawable.primary_button_background)
                joinButton.setTextColor(context.resources.getColor(android.R.color.white, null))
            }

            // Set click listeners
            itemView.setOnClickListener {
                onGroupClick(group)
            }

            joinButton.setOnClickListener {
                // Handle join/leave functionality
                if (group.isJoined == true) {
                    // Leave group
                    // TODO: Implement leave group API call
                    joinButton.text = "Join"
                    joinButton.setBackgroundResource(R.drawable.primary_button_background)
                    joinButton.setTextColor(context.resources.getColor(android.R.color.white, null))
                } else {
                    // Join group
                    // TODO: Implement join group API call
                    joinButton.text = "Joined"
                    joinButton.setBackgroundResource(R.drawable.secondary_button_background)
                    joinButton.setTextColor(context.resources.getColor(android.R.color.black, null))
                }
            }
        }
    }
}
