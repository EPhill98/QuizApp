package com.example.cwquizapp

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView (R.layout.user_stats)
        val userID = intent.getStringExtra("CURRENT_USER_ID")
        val database = FirebaseDatabase.getInstance()
        val userStatsRef = database.getReference("userStats").child(userID!!)

        userStatsRef.get().addOnSuccessListener { dataSnapshot ->
            val historyData = dataSnapshot.child("History").getValue(Int::class.java)
            Log.i("EPDP123", "historyData: $historyData")
        }


        val pieChart: PieChart = findViewById(R.id.pieChart)

        // Prepare the data
        val entries = listOf(
            PieEntry(40f, "Category A"),
            PieEntry(30f, "Category B"),
            PieEntry(20f, "Category C"),
            PieEntry(10f, "Category D")
        )

        val dataSet = PieDataSet(entries, "Categories").apply {
            colors = listOf(
                Color.BLUE,
                Color.RED,
                Color.GREEN,
                Color.MAGENTA
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val pieData = PieData(dataSet)

        // Customize the chart
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 40f
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)

        // Customize the legend


        // Refresh the chart
        pieChart.invalidate()


    }
}