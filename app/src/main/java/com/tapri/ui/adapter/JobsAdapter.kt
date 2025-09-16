package com.tapri.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.network.JobItem

class JobsAdapter(
    private var items: List<JobItem>,
    private var claimedJobs: Set<Long> = emptySet(),
    private val onDetails: (JobItem) -> Unit,
    private val onClaim: (JobItem) -> Unit
) : RecyclerView.Adapter<JobsAdapter.JobViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_job, parent, false)
        return JobViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(items[position], claimedJobs.contains(items[position].id), onDetails, onClaim)
    }

    fun update(newItems: List<JobItem>, newClaimedJobs: Set<Long> = emptySet()) {
        items = newItems
        claimedJobs = newClaimedJobs
        notifyDataSetChanged()
    }

    class JobViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleText)
        private val rate: TextView = itemView.findViewById(R.id.rateText)
        private val info: TextView = itemView.findViewById(R.id.infoText)
        private val claim: TextView = itemView.findViewById(R.id.claimButton)
        private val viewDetails: TextView = itemView.findViewById(R.id.viewDetails)

        fun bind(item: JobItem, isClaimed: Boolean, onDetails: (JobItem) -> Unit, onClaim: (JobItem) -> Unit) {
            title.text = item.title
            rate.text = item.hourlyRate?.let { "${it.toInt()}/hr" } ?: ""
            info.text = item.location ?: ""
            
            if (isClaimed) {
                claim.text = "Claimed"
                claim.setBackgroundResource(R.drawable.claimed_button_background)
                claim.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                claim.isEnabled = false
            } else {
                claim.text = "Claim"
                claim.setBackgroundResource(R.drawable.claim_button_background)
                claim.setTextColor(itemView.context.getColor(android.R.color.white))
                claim.isEnabled = true
            }
            
            viewDetails.setOnClickListener { onDetails(item) }
            claim.setOnClickListener { if (!isClaimed) onClaim(item) }
        }
    }
}
