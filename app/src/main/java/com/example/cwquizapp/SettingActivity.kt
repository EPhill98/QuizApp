package com.example.cwquizapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val userID = intent.getStringExtra("CURRENT_USER_ID") ?: run {
            Log.e("SettingActivity", "CURRENT_USER_ID is missing")
            finish() // Close the activity if no user ID is provided
            return
        }

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userSettingsRef = firebaseDatabase.getReference("userSettings")

        // Get the input field
        val questionNumberInput = findViewById<EditText>(R.id.numberInput)

        // Button to save the input to Firebase
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            val questionNumber = questionNumberInput.text.toString()

            if (questionNumber.isEmpty()) {
                Snackbar.make(questionNumberInput, "Please enter a valid number", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            userSettingsRef.child(userID).child("questionNumber").setValue(questionNumber.toInt())
                .addOnSuccessListener {
                    Snackbar.make(questionNumberInput, "Settings saved successfully", Snackbar.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.e("SettingActivity", "Failed to save settings", e)
                    Snackbar.make(questionNumberInput, "Failed to save settings", Snackbar.LENGTH_LONG).show()
                }
        }
    }
}
