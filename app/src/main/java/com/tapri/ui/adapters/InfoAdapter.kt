package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.InfoItem

class InfoAdapter(private val infoItems: List<InfoItem>) : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    class InfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.infoIcon)
        val titleTextView: TextView = itemView.findViewById(R.id.infoTitle)
        val valueTextView: TextView = itemView.findViewById(R.id.infoValue)
        val descriptionTextView: TextView = itemView.findViewById(R.id.infoDescription)
        val categoryTextView: TextView = itemView.findViewById(R.id.infoCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_info, parent, false)
        return InfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        val infoItem = infoItems[position]
        
        holder.iconImageView.setImageResource(infoItem.iconResId)
        holder.titleTextView.text = infoItem.title
        holder.valueTextView.text = infoItem.value
        holder.descriptionTextView.text = infoItem.description
        holder.categoryTextView.text = infoItem.category
    }

    override fun getItemCount(): Int = infoItems.size
} 