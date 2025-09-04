package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R
import java.text.SimpleDateFormat
import java.util.*

class GroupChatActivity : AppCompatActivity() {
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messagesContainer: LinearLayout
    private lateinit var messagesScrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        // Initialize views
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        messagesContainer = findViewById(R.id.messagesContainer)
        messagesScrollView = findViewById(R.id.messagesScrollView)

        // Get group name from intent
        val groupName = intent.getStringExtra("group_name") ?: "Ola Drivers"

        // Set up click listeners
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<TextView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // More options button
        findViewById<ImageView>(R.id.moreOptionsButton).setOnClickListener {
            Toast.makeText(this, "More options", Toast.LENGTH_SHORT).show()
        }

        // Send button
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Send on Enter key
        messageInput.setOnEditorActionListener { _, _, _ ->
            sendMessage()
            true
        }
    }

    private fun sendMessage() {
        val messageText = messageInput.text.toString().trim()
        if (messageText.isNotEmpty()) {
            addMessage(messageText, true)
            messageInput.text.clear()
            scrollToBottom()
        }
    }

    private fun addMessage(message: String, isOwnMessage: Boolean) {
        val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        
        val messageLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 0
                topMargin = 0
                rightMargin = 0
                bottomMargin = 64 // 16dp bottom margin
            }
            orientation = LinearLayout.HORIZONTAL
            if (isOwnMessage) {
                gravity = android.view.Gravity.END
            }
        }

        if (!isOwnMessage) {
            // Add profile picture for other users
            val profilePic = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(128, 128).apply {
                    marginEnd = 32
                }
                setImageResource(R.drawable.ic_profile)
                setColorFilter(resources.getColor(android.R.color.darker_gray, null))
            }
            messageLayout.addView(profilePic)
        }

        val messageContentLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (isOwnMessage) {
                    marginStart = 240 // 60dp margin
                } else {
                    marginEnd = 240
                }
            }
            orientation = LinearLayout.VERTICAL
        }

        if (!isOwnMessage) {
            // Add sender name for other users
            val senderName = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    leftMargin = 0
                    topMargin = 0
                    rightMargin = 0
                    bottomMargin = 16
                }
                text = "User"
                textSize = 12f
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
            }
            messageContentLayout.addView(senderName)
        }

        // Message bubble
        val messageBubble = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                if (isOwnMessage) {
                    gravity = android.view.Gravity.END
                }
            }
            background = resources.getDrawable(
                if (isOwnMessage) R.drawable.message_bubble_own else R.drawable.message_bubble_other,
                null
            )
            setPadding(48, 48, 48, 48) // 12dp padding
        }

        val messageTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = message
            textSize = 14f
            setTextColor(
                resources.getColor(
                    if (isOwnMessage) android.R.color.white else android.R.color.black,
                    null
                )
            )
            maxWidth = 960 // 240dp max width
        }

        messageBubble.addView(messageTextView)
        messageContentLayout.addView(messageBubble)

        // Time stamp
        val timeStamp = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                leftMargin = 0
                topMargin = 16 // 4dp top margin
                rightMargin = 0
                bottomMargin = 0
                if (isOwnMessage) {
                    gravity = android.view.Gravity.END
                }
            }
            text = currentTime
            textSize = 10f
            setTextColor(resources.getColor(android.R.color.darker_gray, null))
        }

        messageContentLayout.addView(timeStamp)
        messageLayout.addView(messageContentLayout)
        messagesContainer.addView(messageLayout)
    }

    private fun scrollToBottom() {
        messagesScrollView.post {
            messagesScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun setupBottomNavigation() {
        val homeNav = findViewById<LinearLayout>(R.id.homeNav)
        val tapriNav = findViewById<LinearLayout>(R.id.tapriNav)
        val earnNav = findViewById<LinearLayout>(R.id.earnNav)
        val infoNav = findViewById<LinearLayout>(R.id.infoNav)
        val tipsNav = findViewById<LinearLayout>(R.id.tipsNav)
        val earnButton = findViewById<ImageView>(R.id.earnButton)

        // Set Tapri as selected by default
        updateNavigationSelection(tapriNav, true)

        homeNav.setOnClickListener {
            updateNavigationSelection(homeNav, true)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        tapriNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, true)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, GroupsActivity::class.java)
            startActivity(intent)
            finish()
        }

        earnNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, EarnActivity::class.java)
            startActivity(intent)
            finish()
        }

        earnButton.setOnClickListener {
            val intent = Intent(this, EarnActivity::class.java)
            startActivity(intent)
            finish()
        }

        infoNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, true)
            updateNavigationSelection(tipsNav, false)
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
            finish()
        }

        tipsNav.setOnClickListener {
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, false)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, true)
            val intent = Intent(this, TipsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateNavigationSelection(navItem: LinearLayout, isSelected: Boolean) {
        val icon = navItem.getChildAt(0) as ImageView
        val text = navItem.getChildAt(1) as TextView

        if (isSelected) {
            icon.setColorFilter(resources.getColor(R.color.red, null))
            text.setTextColor(resources.getColor(R.color.red, null))
        } else {
            icon.setColorFilter(resources.getColor(android.R.color.black, null))
            text.setTextColor(resources.getColor(android.R.color.black, null))
        }
    }
} 