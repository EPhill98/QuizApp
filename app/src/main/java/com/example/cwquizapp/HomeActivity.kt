package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.getValue


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val root = findViewById<View>(android.R.id.content)
        val currentUserID = intent.getStringExtra("CURRENT_USER_ID")

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users").child(currentUserID!!)

        usersRef.child("email").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userEmail = dataSnapshot.getValue(String::class.java)
                // Use userEmail to display or perform other actions
                val welcomeSnackbar = Snackbar.make(root, "WELCOME $userEmail", Snackbar.LENGTH_LONG)
                welcomeSnackbar.show()
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle errors
                Log.e("HomeActivity", "Error getting user email: ${error.message}")
            }
        })

        usersRef.child("lastLogin").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userLoginTime = dataSnapshot.getValue(Long::class.java)
                val lastLoginTimeTXT = findViewById<TextView>(R.id.lastLoginTxt)

                if (userLoginTime != null) {
                    val formattedTime = formatTimestamp(userLoginTime)
                    lastLoginTimeTXT.text = "$formattedTime"
                } else {
                    lastLoginTimeTXT.text = "Unknown"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "Error posting time: ${error.message}")
            }
        })

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val modelLst = popList()

        val recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
        val layoutManager = LinearLayoutManager(this )
        recyclerView.layoutManager = layoutManager

        val myAdapter = MyApdapter(modelLst)
        recyclerView.adapter = myAdapter

    }

    private fun launchQScreen() {
        val newIntent = Intent(this, QuestionActivity::class.java)
        startActivity(newIntent)
    }

    private fun popList(): ArrayList<MyModel> {
        val lst = ArrayList<MyModel>()
        val myIcons = arrayOf(
            R.drawable.sports, R.drawable.globeicon, R.drawable.food, R.drawable.movie_icon,
            R.drawable.history_icon, R.drawable.science_icon, R.drawable.art_icon, R.drawable.music_icon
        )
        val myCatLst = arrayOf(
            "Sports", "Geography", "Food", "Movies",
            "History", "Science", "Art", "Music"
        )
        for (i in 0..7) {
            val imgModel = MyModel()
            imgModel.setNames((myCatLst[i]))
            imgModel.setImage(myIcons[i])
            lst.add(imgModel)
        }
        lst.sortBy { lst -> lst.modelName}
        return lst
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.toolbar_layout), menu)
        return super.onCreateOptionsMenu(menu)
    }


    private fun formatTimestamp(timestamp: Long): String {
        val date = java.util.Date(timestamp)
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy, hh:mm a", java.util.Locale.getDefault())
        return dateFormat.format(date)
    }

}