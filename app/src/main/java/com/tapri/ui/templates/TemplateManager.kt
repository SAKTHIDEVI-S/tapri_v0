package com.tapri.ui.templates

import android.content.Context
import android.widget.Toast

object TemplateManager {
    
    /**
     * Get a random template for the specified post type
     * Avoids repeating the same template immediately
     */
    fun getRandomTemplate(postType: String): PostTemplate? {
        return TemplateLibrary.getRandomTemplate(postType)
    }
    
    /**
     * Get all available templates for a post type
     */
    fun getAllTemplates(postType: String): List<PostTemplate> {
        return TemplateLibrary.getAllTemplates(postType)
    }
    
    /**
     * Check if the current text is from a template
     */
    fun isTemplateText(text: String, postType: String): Boolean {
        val templates = getAllTemplates(postType)
        return templates.any { template -> 
            text.startsWith(template.text.take(20))
        }
    }
    
    /**
     * Get template count for a post type
     */
    fun getTemplateCount(postType: String): Int {
        return getAllTemplates(postType).size
    }
    
    /**
     * Show template statistics (for debugging/admin purposes)
     */
    fun showTemplateStats(context: Context) {
        val trafficCount = getTemplateCount("Traffic alert")
        val tipCount = getTemplateCount("Share tip")
        val helpCount = getTemplateCount("Ask help")
        
        val message = "Templates available:\n" +
                     "Traffic Alert: $trafficCount\n" +
                     "Share Tip: $tipCount\n" +
                     "Ask Help: $helpCount"
        
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    
    /**
     * Reset template selection history (useful for testing)
     */
    fun resetTemplateHistory() {
        TemplateLibrary.lastSelectedTemplates.clear()
    }
    
    /**
     * Get template by index (for advanced features)
     */
    fun getTemplateByIndex(postType: String, index: Int): PostTemplate? {
        val templates = getAllTemplates(postType)
        return if (index in 0 until templates.size) {
            templates[index]
        } else {
            null
        }
    }
    
    /**
     * Search templates by keyword
     */
    fun searchTemplates(postType: String, keyword: String): List<PostTemplate> {
        val templates = getAllTemplates(postType)
        return templates.filter { template ->
            template.text.contains(keyword, ignoreCase = true) ||
            template.placeholder.contains(keyword, ignoreCase = true) ||
            template.category.contains(keyword, ignoreCase = true)
        }
    }
}
