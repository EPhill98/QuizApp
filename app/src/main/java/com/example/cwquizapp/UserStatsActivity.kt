package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject
import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData

class UserStatsActivity : AppCompatActivity() {

    private lateinit var currentUserID: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (R.layout.user_stats)
        val currentUserID = intent.getStringExtra("CURRENT_USER_ID")
        
        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val database = FirebaseDatabase.getInstance()
        val userStatsRef = database.getReference("userStats").child(currentUserID!!)

        userStatsRef.get().addOnSuccessListener { dataSnapshot ->
            val movieData = dataSnapshot.child("Movies").getValue(Int::class.java)?: 0
            val musicData = dataSnapshot.child("Music").getValue(Int::class.java)?: 0
            val scienceData = dataSnapshot.child("Science").getValue(Int::class.java)?: 0
            val sportsData = dataSnapshot.child("Sports").getValue(Int::class.java)?: 0
            val geographyData = dataSnapshot.child("Geography").getValue(Int::class.java)?: 0
            val historyData = dataSnapshot.child("History").getValue(Int::class.java)?: 0
            val artData = dataSnapshot.child("Art").getValue(Int::class.java)?: 0
            val foodData = dataSnapshot.child("Food").getValue(Int::class.java) ?: 0

            val pieChart: PieChart = findViewById(R.id.pieChart)

            // Prepare the data
            val entries = listOf(
                PieEntry(scienceData.toFloat(), "Science"),
                PieEntry(musicData.toFloat(), "Music"),
                PieEntry(sportsData.toFloat(), "Sports"),
                PieEntry(geographyData.toFloat(), "Geography"),
                PieEntry(historyData.toFloat(), "History"),
                PieEntry(artData.toFloat(), "Art"),
                PieEntry(foodData.toFloat(), "Food"),
                PieEntry(movieData.toFloat(), "Movies")
            )

            val dataSet = PieDataSet(entries, "Categories").apply {
                colors = listOf(
                    Color.BLUE,
                    Color.RED,
                    Color.GREEN,
                    Color.MAGENTA,
                    Color.YELLOW,
                    Color.CYAN,
                    Color.GRAY,
                    Color.DKGRAY
                )
                valueTextColor = Color.WHITE
                valueTextSize = 12f
            }

            val pieData = PieData(dataSet)

            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(Color.BLACK)
            pieChart.setEntryLabelTextSize(12f)

            pieChart.invalidate()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
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

            R.id.home_icon -> {
                val newIntent = Intent(this, HomeActivity::class.java)
                newIntent.putExtra("CURRENT_USER_ID", currentUserID)
                startActivity(newIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}