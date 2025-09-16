 package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.tapri.R
import com.tapri.network.ApiClient
import com.tapri.network.CreateGroupRequest
import com.tapri.network.GroupsApi
import com.tapri.utils.AnimationUtils
import com.tapri.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateGroupActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    private lateinit var groupsApi: GroupsApi
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    // UI Elements
    private lateinit var backButton: ImageView
    private lateinit var groupNameInput: EditText
    private lateinit var groupDescriptionInput: EditText
    private lateinit var groupCategorySpinner: Spinner
    private lateinit var createGroupButton: LinearLayout
    private lateinit var loadingView: LinearLayout
    private lateinit var formContainer: LinearLayout
    
    private var selectedCategory: String = "General"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        
        // Initialize
        sessionManager = SessionManager(this)
        groupsApi = ApiClient.groupsRetrofit(sessionManager).create(GroupsApi::class.java)
        
        // Find views
        backButton = findViewById(R.id.backButton)
        groupNameInput = findViewById(R.id.groupNameInput)
        groupDescriptionInput = findViewById(R.id.groupDescriptionInput)
        groupCategorySpinner = findViewById(R.id.groupCategorySpinner)
        createGroupButton = findViewById(R.id.createGroupButton)
        loadingView = findViewById(R.id.loadingView)
        formContainer = findViewById(R.id.formContainer)
        
        // Set up UI
        setupCategorySpinner()
        setupFormValidation()
        setupClickListeners()
        
        // Show form initially
        showFormState()
    }
    
    private fun setupCategorySpinner() {
        val categories = arrayOf("General", "Transport", "Food Delivery", "Grocery", "Multi-Service", "Business", "Social", "Local")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupCategorySpinner.adapter = adapter
        
        groupCategorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = "General"
            }
        }
    }
    
    private fun setupFormValidation() {
        // Add text change listeners for real-time validation
        groupNameInput.addTextChangedListener { text ->
            updateCreateButtonState()
        }
        
        groupDescriptionInput.addTextChangedListener { text ->
            updateCreateButtonState()
        }
        
        // Initial state
        updateCreateButtonState()
    }
    
    private fun updateCreateButtonState() {
        val name = groupNameInput.text.toString().trim()
        val description = groupDescriptionInput.text.toString().trim()
        
        val isValid = name.isNotEmpty() && description.isNotEmpty()
        
        if (isValid) {
            createGroupButton.alpha = 1.0f
            createGroupButton.isEnabled = true
        } else {
            createGroupButton.alpha = 0.6f
            createGroupButton.isEnabled = false
        }
    }
    
    private fun setupClickListeners() {
        backButton.setOnClickListener {
            AnimationUtils.animateButtonPress(backButton) {
                finish()
            }
        }
        
        createGroupButton.setOnClickListener {
            if (createGroupButton.isEnabled) {
                AnimationUtils.animateButtonPress(createGroupButton) {
                    createGroup()
                }
            }
        }
    }
    
    private fun createGroup() {
        val name = groupNameInput.text.toString().trim()
        val description = groupDescriptionInput.text.toString().trim()
        
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        showLoadingState()
        
        coroutineScope.launch {
            try {
                val newGroup = CreateGroupRequest(
                    name = name,
                    description = description,
                    category = selectedCategory
                )
                
                android.util.Log.d("CreateGroupActivity", "Creating group: $newGroup")
                android.util.Log.d("CreateGroupActivity", "Auth token: ${sessionManager.getAuthToken()}")
                android.util.Log.d("CreateGroupActivity", "Is logged in: ${sessionManager.isLoggedIn()}")
                android.util.Log.d("CreateGroupActivity", "Making API call to create group...")
                android.util.Log.d("CreateGroupActivity", "Base URL: ${com.tapri.utils.Config.getBaseUrl()}")
                android.util.Log.d("CreateGroupActivity", "Groups API base URL: ${com.tapri.utils.Config.getBaseUrl()}groups/")
                val response = groupsApi.createGroup(newGroup)
                android.util.Log.d("CreateGroupActivity", "Create group response: code=${response.code()}")
                android.util.Log.d("CreateGroupActivity", "Response body: ${response.body()}")
                android.util.Log.d("CreateGroupActivity", "Response headers: ${response.headers()}")
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val createdGroup = response.body()
                        android.util.Log.d("CreateGroupActivity", "Group created successfully: $createdGroup")
                        
                        Toast.makeText(this@CreateGroupActivity, "Group created successfully!", Toast.LENGTH_SHORT).show()
                        
                        // Return to GroupsActivity with success result
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        android.util.Log.e("CreateGroupActivity", "Failed to create group: ${response.code()}")
                        android.util.Log.e("CreateGroupActivity", "Response message: ${response.message()}")
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("CreateGroupActivity", "Error body: $errorBody")
                        android.util.Log.e("CreateGroupActivity", "Response headers: ${response.headers()}")
                        android.util.Log.e("CreateGroupActivity", "Full response: ${response}")
                        showFormState()
                        
                        when (response.code()) {
                            401 -> {
                                Toast.makeText(this@CreateGroupActivity, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
                                sessionManager.clearSession()
                                startActivity(Intent(this@CreateGroupActivity, LoginActivity::class.java))
                                finish()
                            }
                            403 -> {
                                Toast.makeText(this@CreateGroupActivity, "Access denied. Please check your permissions.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this@CreateGroupActivity, "Failed to create group. Please try again.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CreateGroupActivity", "Exception while creating group: ${e.message}", e)
                android.util.Log.e("CreateGroupActivity", "Exception type: ${e.javaClass.simpleName}")
                android.util.Log.e("CreateGroupActivity", "Exception stack trace: ${e.stackTraceToString()}")
                withContext(Dispatchers.Main) {
                    showFormState()
                    Toast.makeText(this@CreateGroupActivity, "Network error. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun showLoadingState() {
        formContainer.visibility = android.view.View.GONE
        loadingView.visibility = android.view.View.VISIBLE
    }
    
    private fun showFormState() {
        formContainer.visibility = android.view.View.VISIBLE
        loadingView.visibility = android.view.View.GONE
    }
}

