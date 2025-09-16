# Tapri Template Library

## Overview
The Tapri Template Library provides a collection of predefined text templates for quick post creation. Each post type (Traffic Alert, Share Tip, Ask Help) has 20 unique templates that are randomly selected to provide variety and engagement.

## Features
- **20 templates per post type** (60 total templates)
- **Random selection** with anti-repetition logic
- **Placeholder highlighting** for easy customization
- **Modular design** for easy expansion
- **Template management utilities**

## Template Categories

### 1. Traffic Alert Templates (20 templates)
- Road blocks and closures
- Accident alerts
- Construction warnings
- Weather-related traffic issues
- Event traffic
- Emergency vehicle movements
- Rush hour alerts
- And more...

### 2. Share Tip Templates (20 templates)
- Fuel saving tips
- Route optimization
- Safety advice
- Earning strategies
- Vehicle maintenance
- Customer service tips
- Technology usage
- And more...

### 3. Ask Help Templates (20 templates)
- Emergency assistance
- Technical support
- Vehicle problems
- App issues
- Route guidance
- Spare parts requests
- Safety concerns
- And more...

## Usage

### Basic Usage
```kotlin
// Get a random template
val template = TemplateLibrary.getRandomTemplate("Traffic alert")

// Use the template
contentInput.setText(template.text)
val startIndex = template.text.indexOf(template.placeholder)
val endIndex = startIndex + template.placeholder.length
contentInput.setSelection(startIndex, endIndex)
```

### Template Management
```kotlin
// Get all templates for a post type
val allTemplates = TemplateLibrary.getAllTemplates("Traffic alert")

// Check if text is from a template
val isTemplate = TemplateManager.isTemplateText(userText, "Traffic alert")

// Get template count
val count = TemplateManager.getTemplateCount("Share tip")
```

## Adding New Templates

### 1. Add to TemplateLibrary.kt
```kotlin
// In the appropriate template list (e.g., trafficAlertTemplates)
PostTemplate(
    "🚦 Your new template text with [PLACEHOLDER] here.",
    "[PLACEHOLDER]",
    "traffic_alert"
)
```

### 2. Template Guidelines
- **Use emojis** to make templates visually appealing
- **Include placeholders** in square brackets for user customization
- **Keep placeholders descriptive** (e.g., [ROAD/AREA] not [X])
- **Make templates actionable** and specific to driver needs
- **Vary the tone** - some formal, some casual
- **Include relevant context** for the situation

### 3. Placeholder Conventions
- `[ROAD/AREA]` - Location names
- `[X]` - Numbers/time
- `[LOCATION]` - Specific places
- `[ISSUE]` - Problems or situations
- `[TIP_CONTENT]` - Advice or tips
- `[REASON]` - Explanations
- `[TIME]` - Time periods
- `[ALTERNATE_ROUTE]` - Alternative paths

## Template Structure

Each template follows this structure:
```kotlin
data class PostTemplate(
    val text: String,        // The template text with placeholders
    val placeholder: String, // The first placeholder to highlight
    val category: String     // Template category for organization
)
```

## Anti-Repetition Logic

The system tracks the last selected template for each post type and avoids immediate repetition:
```kotlin
private val lastSelectedTemplates = mutableMapOf<String, Int>()

// When selecting a template
do {
    randomIndex = (0 until templates.size).random()
} while (randomIndex == lastIndex && templates.size > 1)
```

## Integration with CreatePostActivity

The template system integrates seamlessly with the existing post creation flow:

1. **User clicks post type button**
2. **System checks if autofill is appropriate**
3. **Random template is selected**
4. **Template is inserted with placeholder highlighted**
5. **User can edit and customize**
6. **Final text is submitted to backend**

## Future Enhancements

### Backend Integration
- Store templates in database
- Allow dynamic template updates
- User-specific template preferences
- Template usage analytics

### Advanced Features
- Template search and filtering
- Custom template creation
- Template sharing between users
- A/B testing for template effectiveness

### Localization
- Multi-language template support
- Region-specific templates
- Cultural adaptation of templates

## Testing

### Unit Tests
```kotlin
@Test
fun testRandomTemplateSelection() {
    val template1 = TemplateLibrary.getRandomTemplate("Traffic alert")
    val template2 = TemplateLibrary.getRandomTemplate("Traffic alert")
    // Should not be the same immediately
    assertNotEquals(template1, template2)
}
```

### Integration Tests
- Test template autofill in CreatePostActivity
- Test placeholder highlighting
- Test anti-repetition logic
- Test template validation

## Maintenance

### Regular Updates
- Review template effectiveness
- Add new templates based on user feedback
- Remove outdated templates
- Update placeholders for clarity

### Performance
- Templates are loaded once at app startup
- Random selection is O(1) operation
- Memory usage is minimal (60 small strings)

## Troubleshooting

### Common Issues
1. **Templates not loading**: Check import statements
2. **Placeholder not highlighting**: Verify placeholder string matches exactly
3. **Repetition occurring**: Check anti-repetition logic
4. **Templates too long**: Consider shortening for mobile display

### Debug Tools
```kotlin
// Show template statistics
TemplateManager.showTemplateStats(context)

// Reset selection history
TemplateManager.resetTemplateHistory()

// Search templates
val results = TemplateManager.searchTemplates("Traffic alert", "accident")
```

## Contributing

When adding new templates:
1. Follow the established patterns
2. Test with different screen sizes
3. Ensure placeholders are clear
4. Consider cultural sensitivity
5. Update this documentation

## License

This template library is part of the Tapri application and follows the same licensing terms.
