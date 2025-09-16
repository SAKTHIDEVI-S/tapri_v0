package com.tapri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tapri.R
import com.tapri.network.GroupDto

class GroupSelectionAdapter(
    private val context: Context,
    private val groups: MutableList<GroupDto>,
    private val onGroupSelected: (GroupDto) -> Unit
) : RecyclerView.Adapter<GroupSelectionAdapter.GroupViewHolder>() {

    private var selectedGroup: GroupDto? = null

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupAvatar: ImageView = itemView.findViewById(R.id.groupAvatar)
        val groupName: TextView = itemView.findViewById(R.id.groupName)
        val memberCount: TextView = itemView.findViewById(R.id.memberCount)
        val selectionIndicator: ImageView = itemView.findViewById(R.id.selectionIndicator)

        fun bind(group: GroupDto) {
            groupName.text = group.name
            memberCount.text = "${group.membersCount} members"

            // Load group avatar
            if (!group.photoUrl.isNullOrEmpty() && group.photoUrl != "null") {
                Glide.with(context)
                    .load(group.photoUrl)
                    .apply(RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.ic_group)
                        .error(R.drawable.ic_group))
                    .into(groupAvatar)
            } else {
                groupAvatar.setImageResource(R.drawable.ic_group)
            }

            // Show selection indicator
            selectionIndicator.visibility = if (selectedGroup?.id == group.id) View.VISIBLE else View.GONE

            // Handle click
            itemView.setOnClickListener {
                selectedGroup = group
                notifyDataSetChanged()
                onGroupSelected(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_group_selection, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groups[position])
    }

    override fun getItemCount(): Int = groups.size

    fun getSelectedGroup(): GroupDto? = selectedGroup

    fun clearSelection() {
        selectedGroup = null
        notifyDataSetChanged()
    }
}
