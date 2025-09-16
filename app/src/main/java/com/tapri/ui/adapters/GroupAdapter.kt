package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.model.TapriGroup

class GroupAdapter(
    private val groups: List<TapriGroup>,
    private val onJoinClick: (TapriGroup) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {
    
    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupImage: ImageView = view.findViewById(R.id.groupAvatar)
        val groupName: TextView = view.findViewById(R.id.groupName)
        val memberCount: TextView = view.findViewById(R.id.memberCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.groupImage.setImageResource(group.groupAvatar)
        holder.groupName.text = group.name
        holder.memberCount.text = group.followerCount
        
        holder.itemView.setOnClickListener {
            onJoinClick(group)
        }
    }

    override fun getItemCount() = groups.size
}