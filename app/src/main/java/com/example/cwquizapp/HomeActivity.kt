package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        currentUserID = intent.getStringExtra("CURRENT_USER_ID").toString()

        firebaseDatabase = FirebaseDatabase.getInstance()

        val root = findViewById<View>(android.R.id.content)

        // Fetch email and display welcome message
        val usersRef: DatabaseReference = firebaseDatabase.getReference("users").child(currentUserID)
        usersRef.child("email").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userEmail = dataSnapshot.getValue(String::class.java)
                val welcomeMessage = getString(R.string.welcome_message, userEmail)
                val welcomeSnackbar = Snackbar.make(root, welcomeMessage, Snackbar.LENGTH_LONG)
                welcomeSnackbar.show()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", getString(R.string.error_getting_user_email, error.message))
            }
        })

        // Fetch last login time
        usersRef.get().addOnSuccessListener { dataSnapshot ->
            val userLoginTime = dataSnapshot.child("oldLogin").getValue(Long::class.java)
            val lastLoginTimeTXT = findViewById<TextView>(R.id.lastLoginTxt)

            if (userLoginTime != null) {
                val formattedTime = formatTimestamp(userLoginTime)
                lastLoginTimeTXT.text = formattedTime
            } else {
                lastLoginTimeTXT.text = getString(R.string.unknown)
            }
        }

        // Set up the toolbar
        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        // Populate the list and set up the RecyclerView
        val modelLst = popList()
        val recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val myAdapter = MyApdapter(modelLst, currentUserID)
        recyclerView.adapter = myAdapter

        // Update the UI with user stats
    }

    private fun popList(): ArrayList<MyModel> {
        val lst = ArrayList<MyModel>()
        val myIcons = arrayOf(
            R.drawable.sports, R.drawable.globeicon, R.drawable.food, R.drawable.movie_icon,
            R.drawable.history_icon, R.drawable.science_icon, R.drawable.art_icon, R.drawable.music_icon
        )
        val myCatLst = arrayOf(
            getString(R.string.sports_cat),
            getString(R.string.geography_cat),
            getString(R.string.food_cat),
            getString(R.string.movies_cat),
            getString(R.string.history_cat),
            getString(R.string.science_cat),
            getString(R.string.art_cat),
            getString(R.string.music_cat)
        )

        val myCatTagLst = arrayOf(
            21,
            22,
            28,
            11,
            23,
            18,
            25,
            12
        )

        for (i in myCatLst.indices) {
            val imgModel = MyModel()
            imgModel.setNames(myCatLst[i])
            imgModel.setImage(myIcons[i])
            imgModel.setCatTag(myCatTagLst[i])
            lst.add(imgModel)
        }
        lst.sortBy { it.modelCatTag }
        return lst
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault())
        return dateFormat.format(date)
    }

    // Helper function to fetch last category and update UI
    private fun fetchAndDisplayLastCategory() {
        val userStatsRef: DatabaseReference = firebaseDatabase.getReference("userStats").child(
            currentUserID
        )

        userStatsRef.child("lastCategory").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lastCategory = dataSnapshot.getValue(String::class.java) ?: getString(R.string.no_category_selected)
                val lastCategoryTextView = findViewById<TextView>(R.id.lastCatTxt)
                lastCategoryTextView.text = lastCategory
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("KYS154", getString(R.string.error_fetching_last_category, error.message))
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.i("EPDP123", getString(R.string.in_on_start))
        // Fetch and display last category
        fetchAndDisplayLastCategory()
    }

    override fun onResume() {
        super.onResume()

        Log.i("EPDP123", getString(R.string.in_on_resume))

        // Fetch and display last category again on resume
        fetchAndDisplayLastCategory()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_icon -> {
                val newIntent = Intent(this, SettingActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
            R.id.stats -> {
                val newIntent = Intent(this, UserStatsActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
