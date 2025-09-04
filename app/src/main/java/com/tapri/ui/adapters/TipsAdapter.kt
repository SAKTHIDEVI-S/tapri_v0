package com.tapri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.model.Tip

class TipsAdapter(private val tips: List<Tip>) : RecyclerView.Adapter<TipsAdapter.TipViewHolder>() {

    class TipViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.tipIcon)
        val titleTextView: TextView = itemView.findViewById(R.id.tipTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tipDescription)
        val categoryTextView: TextView = itemView.findViewById(R.id.tipCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip, parent, false)
        return TipViewHolder(view)
    }

    override fun onBindViewHolder(holder: TipViewHolder, position: Int) {
        val tip = tips[position]
        
        holder.iconImageView.setImageResource(tip.iconResId)
        holder.titleTextView.text = tip.title
        holder.descriptionTextView.text = tip.description
        holder.categoryTextView.text = tip.category
    }

    override fun getItemCount(): Int = tips.size
} 