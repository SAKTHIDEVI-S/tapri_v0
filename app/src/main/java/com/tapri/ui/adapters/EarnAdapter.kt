package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.EarnOpportunity

class EarnAdapter(private val earnOpportunities: List<EarnOpportunity>) : RecyclerView.Adapter<EarnAdapter.EarnViewHolder>() {

    class EarnViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.earnIcon)
        val titleTextView: TextView = itemView.findViewById(R.id.earnTitle)
        val subtitleTextView: TextView = itemView.findViewById(R.id.earnSubtitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.earnDescription)
        val categoryTextView: TextView = itemView.findViewById(R.id.earnCategory)
        val rewardTextView: TextView = itemView.findViewById(R.id.earnReward)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EarnViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_earn, parent, false)
        return EarnViewHolder(view)
    }

    override fun onBindViewHolder(holder: EarnViewHolder, position: Int) {
        val earnOpportunity = earnOpportunities[position]
        
        holder.iconImageView.setImageResource(earnOpportunity.iconResId)
        holder.titleTextView.text = earnOpportunity.title
        holder.subtitleTextView.text = earnOpportunity.subtitle
        holder.descriptionTextView.text = earnOpportunity.description
        holder.categoryTextView.text = earnOpportunity.category
        holder.rewardTextView.text = earnOpportunity.reward
    }

    override fun getItemCount(): Int = earnOpportunities.size
} 