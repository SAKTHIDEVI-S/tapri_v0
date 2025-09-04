package com.tapri.ui

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tapri.R

class GroupsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        // Find views by ID
        val backButton = findViewById<TextView>(R.id.backButton)
        val createPostButton = findViewById<LinearLayout>(R.id.createPostButton)
        val newGroupButton = findViewById<android.widget.ImageView>(R.id.newGroupButton)
        val exploreMoreButton = findViewById<LinearLayout>(R.id.exploreMoreButton)
        
        // Join buttons
        val joinButton1 = findViewById<TextView>(R.id.joinButton1)
        val joinButton2 = findViewById<TextView>(R.id.joinButton2)
        val joinButton3 = findViewById<TextView>(R.id.joinButton3)
        
        // Bottom navigation
        val homeNav = findViewById<LinearLayout>(R.id.homeNav)
        val tapriNav = findViewById<LinearLayout>(R.id.tapriNav)
        val earnNav = findViewById<LinearLayout>(R.id.earnNav)
        val infoNav = findViewById<LinearLayout>(R.id.infoNav)
        val tipsNav = findViewById<LinearLayout>(R.id.tipsNav)
        val earnButton = findViewById<android.widget.ImageView>(R.id.earnButton)

        // Set up button click listeners
        backButton.setOnClickListener {
            finish()
        }

        createPostButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }

        newGroupButton.setOnClickListener {
            Toast.makeText(this, "Create new group", Toast.LENGTH_SHORT).show()
        }

        exploreMoreButton.setOnClickListener {
            Toast.makeText(this, "Explore more groups", Toast.LENGTH_SHORT).show()
        }

        // Set up join button functionality
        setupJoinButton(joinButton1)
        setupJoinButton(joinButton2)
        setupJoinButton(joinButton3)

        // Set up "My groups" click listeners
        setupMyGroupsClickListeners()

        // Set up bottom navigation
        setupBottomNavigation(homeNav, tapriNav, earnNav, infoNav, tipsNav, earnButton)
    }

    private fun setupJoinButton(joinButton: TextView) {
        joinButton.setOnClickListener {
            if (joinButton.text == "Join") {
                joinButton.text = "Joined"
                joinButton.setBackgroundResource(R.drawable.secondary_button_background)
                joinButton.setTextColor(resources.getColor(android.R.color.black, null))
                Toast.makeText(this, "Joined group successfully", Toast.LENGTH_SHORT).show()
            } else {
                joinButton.text = "Join"
                joinButton.setBackgroundResource(R.drawable.primary_button_background)
                joinButton.setTextColor(resources.getColor(android.R.color.white, null))
                Toast.makeText(this, "Left group", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupMyGroupsClickListeners() {
        // Find the "My groups" items by their parent containers
        val myGroupsContainer = findViewById<LinearLayout>(R.id.myGroupsContainer)
        
        // Get the first and second group items (they are direct children of the container)
        val groupItem1 = myGroupsContainer.getChildAt(0) as LinearLayout
        val groupItem2 = myGroupsContainer.getChildAt(1) as LinearLayout
        
        groupItem1.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("group_name", "Ola Drivers")
            startActivity(intent)
        }
        
        groupItem2.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            intent.putExtra("group_name", "Ola Drivers")
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation(
        homeNav: LinearLayout,
        tapriNav: LinearLayout,
        earnNav: LinearLayout,
        infoNav: LinearLayout,
        tipsNav: LinearLayout,
        earnButton: android.widget.ImageView
    ) {
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
            // Already on Tapri screen
            updateNavigationSelection(homeNav, false)
            updateNavigationSelection(tapriNav, true)
            updateNavigationSelection(infoNav, false)
            updateNavigationSelection(tipsNav, false)
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
        val icon = navItem.getChildAt(0) as android.widget.ImageView
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