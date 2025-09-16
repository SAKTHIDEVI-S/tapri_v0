package com.tapri.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tapri.R
import com.tapri.ui.adapters.GroupChatAdapter
import com.tapri.network.ApiClient
import com.tapri.network.GroupsApi
import com.tapri.network.GroupMessageDto
import com.tapri.network.SendGroupMessageRequest
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroupChatActivity : AppCompatActivity() {
    
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageView
    private lateinit var groupNameTextView: TextView
    private lateinit var backButton: ImageView
    private lateinit var chatAdapter: GroupChatAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var groupsApi: GroupsApi
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var groupId: Long = 0
    private var groupName: String = ""
    private val messages = mutableListOf<GroupMessageDto>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)
        
        // Initialize
        sessionManager = SessionManager(this)
        groupsApi = ApiClient.groupsRetrofit(sessionManager).create(GroupsApi::class.java)
        
        // Get group info from intent
        groupId = intent.getLongExtra("groupId", 0)
        groupName = intent.getStringExtra("groupName") ?: "Group Chat"
        
        // Find views
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        groupNameTextView = findViewById(R.id.groupNameTextView)
        backButton = findViewById(R.id.backButton)
        
        // Set group name
        groupNameTextView.text = groupName
        
        // Set up RecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = GroupChatAdapter(messages, sessionManager)
        chatRecyclerView.adapter = chatAdapter
        
        // Load messages
        loadMessages()
        
        // Set up click listeners
        backButton.setOnClickListener {
            finish()
        }
        
        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                messageInput.text.clear()
            }
        }
    }
    
    private fun loadMessages() {
        coroutineScope.launch {
            try {
                android.util.Log.d("GroupChatActivity", "Loading messages for group $groupId")
                android.util.Log.d("GroupChatActivity", "Auth token: ${sessionManager.getAuthToken()}")
                android.util.Log.d("GroupChatActivity", "Is logged in: ${sessionManager.isLoggedIn()}")
                
                val response = groupsApi.getGroupMessages(groupId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val messagesResponse = response.body()
                        if (messagesResponse != null) {
                            messages.clear()
                            messages.addAll(messagesResponse.messages.reversed()) // Reverse to show oldest first
                            chatAdapter.notifyDataSetChanged()
                            chatRecyclerView.smoothScrollToPosition(messages.size - 1)
                            
                            // Mark messages as read
                            markMessagesAsRead()
                        }
                    } else {
                        android.util.Log.e("GroupChatActivity", "Failed to load messages: ${response.code()}")
                        android.util.Log.e("GroupChatActivity", "Response headers: ${response.headers()}")
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("GroupChatActivity", "Error body: $errorBody")
                        
                        when (response.code()) {
                            401, 403 -> {
                                android.util.Log.w("GroupChatActivity", "Authentication error, attempting token refresh...")
                                if (sessionManager.canRefreshToken()) {
                                    attemptTokenRefreshAndRetry()
                                } else {
                                    Toast.makeText(this@GroupChatActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                    sessionManager.clearSession()
                                    finish()
                                }
                            }
                            else -> {
                                Toast.makeText(this@GroupChatActivity, "Failed to load messages: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupChatActivity", "Exception while loading messages: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    when {
                        e.message?.contains("401") == true || e.message?.contains("403") == true -> {
                            if (sessionManager.canRefreshToken()) {
                                attemptTokenRefreshAndRetry()
                            } else {
                                Toast.makeText(this@GroupChatActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                finish()
                            }
                        }
                        else -> {
                            Toast.makeText(this@GroupChatActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun sendMessage(content: String) {
        coroutineScope.launch {
            try {
                val request = SendGroupMessageRequest(content = content)
                val response = groupsApi.sendGroupMessage(groupId, request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val newMessage = response.body()
                        if (newMessage != null) {
                            messages.add(newMessage)
                            chatAdapter.notifyItemInserted(messages.size - 1)
                            chatRecyclerView.smoothScrollToPosition(messages.size - 1)
                        }
                    } else {
                        android.util.Log.e("GroupChatActivity", "Failed to send message: ${response.code()}")
                        when (response.code()) {
                            401, 403 -> {
                                android.util.Log.w("GroupChatActivity", "Authentication error while sending message, attempting token refresh...")
                                if (sessionManager.canRefreshToken()) {
                                    attemptTokenRefreshAndRetry()
                                } else {
                                    Toast.makeText(this@GroupChatActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                    sessionManager.clearSession()
                                    finish()
                                }
                            }
                            else -> {
                                Toast.makeText(this@GroupChatActivity, "Failed to send message", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("GroupChatActivity", "Exception while sending message: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    when {
                        e.message?.contains("401") == true || e.message?.contains("403") == true -> {
                            if (sessionManager.canRefreshToken()) {
                                attemptTokenRefreshAndRetry()
                            } else {
                                Toast.makeText(this@GroupChatActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                finish()
                            }
                        }
                        else -> {
                            Toast.makeText(this@GroupChatActivity, "Network error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    
    private fun markMessagesAsRead() {
        coroutineScope.launch {
            try {
                groupsApi.markMessagesAsRead(groupId)
            } catch (e: Exception) {
                android.util.Log.e("GroupChatActivity", "Exception while marking messages as read: ${e.message}", e)
            }
        }
    }
    
    private fun attemptTokenRefreshAndRetry() {
        android.util.Log.d("GroupChatActivity", "Attempting token refresh and retry...")
        com.tapri.utils.TokenRefreshHelper.refreshTokenAsync(
            sessionManager = sessionManager,
            coroutineScope = coroutineScope,
            onSuccess = {
                android.util.Log.d("GroupChatActivity", "Token refreshed successfully, retrying load messages")
                loadMessages()
            },
            onFailure = {
                android.util.Log.e("GroupChatActivity", "Token refresh failed, redirecting to login")
                sessionManager.clearSession()
                Toast.makeText(this@GroupChatActivity, "Session expired, please login again", Toast.LENGTH_SHORT).show()
                finish()
            }
        )
    }
}