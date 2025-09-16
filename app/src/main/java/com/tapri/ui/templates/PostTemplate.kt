package com.tapri.ui.templates

data class PostTemplate(
    val text: String,
    val placeholder: String,
    val category: String
)

object TemplateLibrary {
    
    // Traffic Alert Templates
    val trafficAlertTemplates = listOf(
        PostTemplate(
            "🚦 Traffic Alert: Heavy traffic on [ROAD/AREA]. Expected delay of [X] mins. Alternate route: [OPTIONAL].",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "⚠️ Road Block: [ROAD/AREA] is completely blocked due to [REASON]. Avoid this route for next [X] hours.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚧 Construction Alert: Road work on [ROAD/AREA] causing [X] min delays. Use [ALTERNATE_ROUTE] instead.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚗 Accident Alert: Minor accident on [ROAD/AREA] near [LANDMARK]. Traffic moving slowly. ETA +[X] mins.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🌧️ Weather Alert: Heavy rain on [ROAD/AREA] causing slippery roads. Drive carefully! Speed limit reduced.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚌 Bus Breakdown: Public transport delayed on [ROAD/AREA]. Expect [X] min wait. Consider other routes.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🎪 Event Traffic: [EVENT_NAME] causing heavy traffic around [AREA]. Park at [PARKING_LOCATION] instead.",
            "[AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚨 Emergency Vehicle: Ambulance/Fire truck on [ROAD/AREA]. Please give way. Traffic backed up [X] km.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🛣️ Road Closure: [ROAD/AREA] closed for [REASON] until [TIME]. Use [DETOUR_ROUTE] as alternative.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚦 Signal Malfunction: Traffic lights not working at [INTERSECTION]. Manual control in progress. Drive carefully.",
            "[INTERSECTION]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚛 Truck Breakdown: Large vehicle blocking [ROAD/AREA]. Recovery vehicle en route. Avoid this section.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🌊 Flood Alert: Water logging on [ROAD/AREA]. Avoid low-lying areas. Use elevated routes only.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚇 Metro Work: Underground construction affecting [AREA] traffic. Surface roads congested. Plan accordingly.",
            "[AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🎯 VIP Movement: Security convoy on [ROAD/AREA]. Temporary restrictions. Normal traffic resumes in [X] mins.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚧 Bridge Repair: [BRIDGE_NAME] under maintenance. One-way traffic only. Expect [X] min delays.",
            "[BRIDGE_NAME]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚦 Rush Hour Alert: Peak traffic on [ROAD/AREA]. Consider leaving [X] mins early or taking [ALTERNATE_ROUTE].",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚌 Bus Strike: Public transport disrupted on [ROAD/AREA]. More private vehicles on road. Expect delays.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🎪 Festival Traffic: [FESTIVAL_NAME] celebrations causing heavy traffic in [AREA]. Park at [PARKING_AREA].",
            "[AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🚨 Police Checkpoint: Random checks on [ROAD/AREA]. Have documents ready. Brief delays expected.",
            "[ROAD/AREA]",
            "traffic_alert"
        ),
        PostTemplate(
            "🛣️ New Route Open: [NEW_ROAD] now operational. Should reduce traffic on [OLD_ROAD]. Try it out!",
            "[NEW_ROAD]",
            "traffic_alert"
        )
    )
    
    // Share Tip Templates
    val shareTipTemplates = listOf(
        PostTemplate(
            "💡 Tip for Drivers: To save fuel, [TIP_CONTENT]. This can help reduce costs and improve earnings.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚗 Driving Tip: [TIP_CONTENT] can help you avoid traffic fines and stay safe on the road.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "⛽ Fuel Saving: [TIP_CONTENT]. Every drop saved is money earned! Share your own tips below.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🛣️ Route Optimization: [TIP_CONTENT]. This trick has saved me [X] minutes daily. Try it out!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚦 Traffic Hack: [TIP_CONTENT]. Works best during [TIME_PERIOD]. Let me know if it helps you!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "💰 Earning Tip: [TIP_CONTENT]. This strategy increased my daily earnings by [X]%. Worth trying!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🅿️ Parking Tip: [TIP_CONTENT]. Saves time and money. Share your parking secrets too!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚗 Maintenance Tip: [TIP_CONTENT]. Regular care prevents costly repairs. Your car will thank you!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🌧️ Weather Driving: [TIP_CONTENT]. Safety first, especially during [WEATHER_CONDITION]. Drive safe!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "📱 App Tip: [TIP_CONTENT]. This feature has made my driving experience much better. Check it out!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚛 Heavy Vehicle Tip: [TIP_CONTENT]. Important for sharing the road safely with trucks and buses.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🕐 Time Management: [TIP_CONTENT]. Planning ahead saves both time and fuel. Every minute counts!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚗 Customer Service: [TIP_CONTENT]. Happy customers mean better ratings and more rides.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🛡️ Safety First: [TIP_CONTENT]. Nothing is more important than reaching home safely every day.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "💰 Cost Cutting: [TIP_CONTENT]. Small savings add up to big profits over time. Every rupee matters!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚗 Vehicle Care: [TIP_CONTENT]. A well-maintained vehicle is more reliable and fuel-efficient.",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "📊 Performance Tip: [TIP_CONTENT]. This has improved my driving efficiency significantly. Try it!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🌍 Eco Driving: [TIP_CONTENT]. Good for the environment and your wallet. Win-win situation!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "🚗 Technology Tip: [TIP_CONTENT]. Modern cars have features that can save you money. Use them!",
            "[TIP_CONTENT]",
            "share_tip"
        ),
        PostTemplate(
            "💼 Business Tip: [TIP_CONTENT]. Running a successful driving business requires smart strategies.",
            "[TIP_CONTENT]",
            "share_tip"
        )
    )
    
    // Ask Help Templates
    val askHelpTemplates = listOf(
        PostTemplate(
            "🙋 Need Help: My [ISSUE, e.g., car broke down / app payment issue] at [LOCATION]. Any nearby drivers who can assist?",
            "[ISSUE, e.g., car broke down / app payment issue]",
            "ask_help"
        ),
        PostTemplate(
            "🚨 Emergency Help: [ISSUE] at [LOCATION]. Urgent assistance needed. Will pay for help!",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🛠️ Technical Support: Having trouble with [ISSUE] at [LOCATION]. Any experienced drivers around?",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Vehicle Problem: [ISSUE] near [LOCATION]. Need quick fix or tow service. Can anyone help?",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "📱 App Issue: [ISSUE] preventing me from working. At [LOCATION]. Any tech-savvy drivers nearby?",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🅿️ Parking Help: Need to find parking near [LOCATION]. Any suggestions? [SPECIFIC_NEED]",
            "[SPECIFIC_NEED]",
            "ask_help"
        ),
        PostTemplate(
            "🛣️ Route Guidance: Lost near [LOCATION]. Need directions to [DESTINATION]. Can someone guide me?",
            "[DESTINATION]",
            "ask_help"
        ),
        PostTemplate(
            "💰 Payment Issue: [ISSUE] with [PAYMENT_METHOD] at [LOCATION]. Need immediate resolution.",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Spare Parts: Need [PART_NAME] urgently at [LOCATION]. Anyone know where to get it nearby?",
            "[PART_NAME]",
            "ask_help"
        ),
        PostTemplate(
            "🛡️ Safety Concern: [ISSUE] at [LOCATION]. Feeling unsafe. Any drivers in the area?",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "📋 Documentation: Need help with [DOCUMENT_ISSUE] at [LOCATION]. Any experienced drivers around?",
            "[DOCUMENT_ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Maintenance: [ISSUE] with my vehicle at [LOCATION]. Need reliable mechanic recommendation.",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "📱 Technology: [ISSUE] with [APP/DEVICE] at [LOCATION]. Tech support needed urgently.",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🛣️ Traffic Info: Stuck in traffic at [LOCATION]. Any updates on [ROAD_CONDITION]?",
            "[ROAD_CONDITION]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Emergency Kit: Need [ITEM] urgently at [LOCATION]. Anyone have spare or know where to get it?",
            "[ITEM]",
            "ask_help"
        ),
        PostTemplate(
            "📋 Legal Help: [ISSUE] with [AUTHORITY] at [LOCATION]. Need advice from experienced drivers.",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Insurance: [ISSUE] with insurance claim at [LOCATION]. Need guidance on [SPECIFIC_HELP].",
            "[SPECIFIC_HELP]",
            "ask_help"
        ),
        PostTemplate(
            "🛠️ Tool Needed: Need [TOOL_NAME] to fix [ISSUE] at [LOCATION]. Can anyone lend or help?",
            "[TOOL_NAME]",
            "ask_help"
        ),
        PostTemplate(
            "📱 Connectivity: [ISSUE] with internet/mobile at [LOCATION]. Need to contact [PERSON]. Help!",
            "[ISSUE]",
            "ask_help"
        ),
        PostTemplate(
            "🚗 Fuel Issue: [ISSUE] with fuel system at [LOCATION]. Need immediate assistance. Emergency!",
            "[ISSUE]",
            "ask_help"
        )
    )
    
    // Template selection tracking
    internal val lastSelectedTemplates = mutableMapOf<String, Int>()
    
    fun getRandomTemplate(postType: String): PostTemplate? {
        val templates = when (postType) {
            "Traffic alert" -> trafficAlertTemplates
            "Ask help" -> askHelpTemplates
            "Share tip" -> shareTipTemplates
            else -> return null
        }
        
        if (templates.isEmpty()) return null
        
        val lastIndex = lastSelectedTemplates[postType] ?: -1
        var randomIndex: Int
        
        // Avoid repeating the same template immediately
        if (templates.size == 1) {
            randomIndex = 0
        } else {
            do {
                randomIndex = (0 until templates.size).random()
            } while (randomIndex == lastIndex)
        }
        
        lastSelectedTemplates[postType] = randomIndex
        return templates[randomIndex]
    }
    
    fun getAllTemplates(postType: String): List<PostTemplate> {
        return when (postType) {
            "Traffic alert" -> trafficAlertTemplates
            "Ask help" -> askHelpTemplates
            "Share tip" -> shareTipTemplates
            else -> emptyList()
        }
    }
}
