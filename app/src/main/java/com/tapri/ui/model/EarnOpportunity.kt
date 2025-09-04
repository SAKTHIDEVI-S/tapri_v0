package com.tapri.ui.model

data class EarnOpportunity(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val category: String,
    val iconResId: Int,
    val reward: String
) 