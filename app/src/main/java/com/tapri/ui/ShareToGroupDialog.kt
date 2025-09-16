package com.tapri.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.GroupsApi
import com.tapri.network.GroupDto
import com.tapri.network.PostsApi
import com.tapri.ui.adapters.GroupSelectionAdapter
import com.tapri.utils.SessionManager
import kotlinx.coroutines.*

class ShareToGroupDialog(
    private val context: Context,
    private val postId: Long,
    private val onShared: () -> Unit
) : Dialog(context) {

    private lateinit var sessionManager: SessionManager
    private lateinit var groupsApi: GroupsApi
    private lateinit var postsApi: PostsApi
    private lateinit var groupsRecyclerView: RecyclerView
    private lateinit var loadingContainer: LinearLayout
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorMessageView: TextView
    private lateinit var retryButton: TextView
    private lateinit var cancelButton: TextView
    private lateinit var shareButton: TextView
    private lateinit var loadingGifView: ImageView
    private lateinit var groupSelectionAdapter: GroupSelectionAdapter
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var selectedGroup: GroupDto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_share_to_group)

        // Initialize
        sessionManager = SessionManager(context)
        groupsApi = ApiClient.postsRetrofit(sessionManager).create(GroupsApi::class.java)
        postsApi = ApiClient.postsRetrofit(sessionManager).create(PostsApi::class.java)

        // Find views
        groupsRecyclerView = findViewById(R.id.groupsRecyclerView)
        loadingContainer = findViewById(R.id.loadingContainer)
        errorContainer = findViewById(R.id.errorContainer)
        errorMessageView = findViewById(R.id.errorMessageView)
        retryButton = findViewById(R.id.retryButton)
        cancelButton = findViewById(R.id.cancelButton)
        shareButton = findViewById(R.id.shareButton)
        loadingGifView = findViewById(R.id.loadingGifView)

        setupRecyclerView()
        setupClickListeners()
        loadGroups()
    }

    private fun setupRecyclerView() {
        groupsRecyclerView.layoutManager = LinearLayoutManager(context)
        groupSelectionAdapter = GroupSelectionAdapter(
            context,
            mutableListOf(),
            onGroupSelected = { group ->
                selectedGroup = group
                shareButton.isEnabled = true
            }
        )
        groupsRecyclerView.adapter = groupSelectionAdapter
    }

    private fun setupClickListeners() {
        cancelButton.setOnClickListener {
            dismiss()
        }

        retryButton.setOnClickListener {
            loadGroups()
        }

        shareButton.setOnClickListener {
            selectedGroup?.let { group ->
                shareToGroup(group)
            }
        }
    }

    private fun loadGroups() {
        showLoadingState()
        coroutineScope.launch {
            try {
                val response = groupsApi.getGroups()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val groups = response.body() ?: emptyList()
                        groupSelectionAdapter = GroupSelectionAdapter(
                            context,
                            groups.toMutableList(),
                            onGroupSelected = { group ->
                                selectedGroup = group
                                shareButton.isEnabled = true
                            }
                        )
                        groupsRecyclerView.adapter = groupSelectionAdapter
                        showContentState()
                    } else {
                        showErrorState("Failed to load groups: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorState("Network error: ${e.message}")
                }
            }
        }
    }

    private fun shareToGroup(group: GroupDto) {
        showLoadingState()
        coroutineScope.launch {
            try {
                val response = postsApi.sharePostToGroup(postId, com.tapri.network.ShareToGroupRequest(group.id ?: 0L))
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        onShared()
                        dismiss()
                    } else {
                        showErrorState("Failed to share to group: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorState("Network error: ${e.message}")
                }
            }
        }
    }

    private fun showLoadingState() {
        Glide.with(context)
            .load(R.drawable.loading)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(loadingGifView)

        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        groupsRecyclerView.visibility = View.GONE
    }

    private fun showContentState() {
        loadingContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE
        groupsRecyclerView.visibility = View.VISIBLE
    }

    private fun showErrorState(message: String) {
        loadingContainer.visibility = View.GONE
        errorContainer.visibility = View.VISIBLE
        errorMessageView.text = message
        groupsRecyclerView.visibility = View.GONE
    }

    override fun dismiss() {
        super.dismiss()
        coroutineScope.cancel()
    }
}