package com.tapri.utils

/**
 * Feature flags for Tapri v0 release
 * Controls which features are enabled/disabled
 */
object FeatureFlags {
    
    // Core features - always enabled
    const val COMMUNITY_FEATURES = true
    const val GROUPS_FEATURES = true
    const val PROFILE_FEATURES = true
    
    // Features disabled for v0 release
    const val EARNINGS_FEATURES = false
    const val INFO_FEATURES = false
    
    // Coming soon screens
    const val SHOW_COMING_SOON_EARN = true
    const val SHOW_COMING_SOON_INFO = true
    
    // Profile sections
    const val SHOW_EARNINGS_IN_PROFILE = false
    const val SHOW_MY_APPS_IN_PROFILE = false
}
