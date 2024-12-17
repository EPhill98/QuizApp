package com.example.cwquizapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.FirebaseDatabase

class UserStatsActivity : AppCompatActivity() {

    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_stats)

        // Initialize the current user ID
        currentUserID = intent.getStringExtra("CURRENT_USER_ID") ?: run {
            Log.e(TAG, "Current User ID is null!")
            return
        }

        // Set up toolbar
        setupToolbar()

        // Fetch and display user statistics
        setupPieChart()

        // Set up and populate the RecyclerView
        setupRecyclerView()
    }

    private fun setupToolbar() {
        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)
    }

    private fun setupPieChart() {
        val pieChart: PieChart = findViewById(R.id.pieChart)
        val database = FirebaseDatabase.getInstance()
        val userStatsRef = database.getReference("userStats").child(currentUserID)

        userStatsRef.get()
            .addOnSuccessListener { dataSnapshot ->
                val entries = listOf(
                    PieEntry(dataSnapshot.child("Science").getValue(Int::class.java)?.toFloat() ?: 0f, "Science"),
                    PieEntry(dataSnapshot.child("Music").getValue(Int::class.java)?.toFloat() ?: 0f, "Music"),
                    PieEntry(dataSnapshot.child("Sports").getValue(Int::class.java)?.toFloat() ?: 0f, "Sports"),
                    PieEntry(dataSnapshot.child("Geography").getValue(Int::class.java)?.toFloat() ?: 0f, "Geography"),
                    PieEntry(dataSnapshot.child("History").getValue(Int::class.java)?.toFloat() ?: 0f, "History"),
                    PieEntry(dataSnapshot.child("Art").getValue(Int::class.java)?.toFloat() ?: 0f, "Art"),
                    PieEntry(dataSnapshot.child("Food").getValue(Int::class.java)?.toFloat() ?: 0f, "Food"),
                    PieEntry(dataSnapshot.child("Movies").getValue(Int::class.java)?.toFloat() ?: 0f, "Movies")
                )

                val dataSet = PieDataSet(entries, "Categories").apply {
                    colors = listOf(
                        Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA,
                        Color.YELLOW, Color.CYAN, Color.GRAY, Color.DKGRAY
                    )
                    valueTextColor = Color.WHITE
                    valueTextSize = 12f
                }

                pieChart.apply {
                    data = PieData(dataSet)
                    description.isEnabled = false
                    setEntryLabelColor(Color.BLACK)
                    setEntryLabelTextSize(12f)
                    invalidate()
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to fetch user stats", exception)
            }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.my_recycler_view_question_history)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val myAdapter = MyApdapterQuestionHistory(mutableListOf())
        recyclerView.adapter = myAdapter

        populateQuestionHistory { modelList ->
            myAdapter.updateList(modelList)
        }
    }

    private fun populateQuestionHistory(callback: (MutableList<MyModelQuestionHist>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val questionHistoryRef = database.getReference("userQuestionHistory").child(currentUserID)

        questionHistoryRef.get()
            .addOnSuccessListener { dataSnapshot ->
                val questionList = dataSnapshot.children.mapNotNull { snapshot ->
                    val question = snapshot.child("question").getValue(String::class.java)
                    val correctAnswer = snapshot.child("correctAnswer").getValue(String::class.java)
                    val userAnswer = snapshot.child("answer").getValue(String::class.java)

                    if (question != null && correctAnswer != null && userAnswer != null) {
                        MyModelQuestionHist().apply {
                            setQuestions(question)
                            setCorrectAnswers(correctAnswer)
                            setUserAnswers(userAnswer)
                        }
                    } else null
                }.toMutableList()

                callback(questionList.asReversed())
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to fetch question history", exception)
                callback(mutableListOf())
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val newIntent = Intent(this, when (item.itemId) {
            R.id.settings_icon -> SettingActivity::class.java
            R.id.stats -> UserStatsActivity::class.java
            R.id.home_icon -> HomeActivity::class.java
            else -> return super.onOptionsItemSelected(item)
        })

        newIntent.putExtra("CURRENT_USER_ID", currentUserID)
        startActivity(newIntent)
        return true
    }

    companion object {
        private const val TAG = "UserStatsActivity"
    }
}
