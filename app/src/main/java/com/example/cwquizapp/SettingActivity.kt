package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class SettingActivity : AppCompatActivity() {

    private lateinit var userID: String // Declare userID as a class member

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        // Get user ID passed from previous activity
        userID = intent.getStringExtra("CURRENT_USER_ID") ?: run {
            Log.e("SettingActivity", "CURRENT_USER_ID is missing")
            finish() // Close the activity if no user ID is provided
            return
        }

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val userSettingsRef = firebaseDatabase.getReference("userSettings")

        // Get the input field
        val questionNumberInput = findViewById<EditText>(R.id.numberInput)
        val typeToggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.question_type_group)
        val difficultyToggleGroup = findViewById<MaterialButtonToggleGroup>(R.id.question_difficulty_group)

        // Button to save the input to Firebase
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            // Validate question number
            val questionNumber = questionNumberInput.text.toString()
            if (questionNumber.isEmpty() && typeToggleGroup.checkedButtonId == -1) {
                Snackbar.make(
                    saveButton,
                    "Please enter a valid number or select a question type",
                    Snackbar.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Update question number if provided
            if (questionNumber.isNotEmpty()) {
                userSettingsRef.child(userID).child("questionNumber")
                    .setValue(questionNumber.toInt())
                    .addOnSuccessListener {
                        Snackbar.make(
                            saveButton,
                            "Question number saved successfully",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SettingActivity", "Failed to save question number", e)
                        Snackbar.make(
                            saveButton,
                            "Failed to save question number",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }

            // Update question type if a selection is made
            val selectedType = when (typeToggleGroup.checkedButtonId) {
                R.id.true_false_choice -> "boolean"
                R.id.any_choice -> "any"
                R.id.multiple_choice_choice -> "multiple"
                else -> null
            }

            if (selectedType != null) {
                userSettingsRef.child(userID).child("questionType")
                    .setValue(selectedType)
                    .addOnSuccessListener {
                        Snackbar.make(
                            saveButton,
                            "Question type saved successfully",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SettingActivity", "Failed to save question type", e)
                        Snackbar.make(
                            saveButton,
                            "Failed to save question type",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }

            // Update question difficulty if a selection is made
            val selectedDifficulty = when (difficultyToggleGroup.checkedButtonId) {
                R.id.easy_q_choice -> "easy"
                R.id.mediumn_q_choice -> "medium"
                R.id.hard_q_choice -> "hard"
                else -> null
            }

            if (selectedDifficulty != null) {
                userSettingsRef.child(userID).child("questionDifficulty")
                    .setValue(selectedDifficulty)
                    .addOnSuccessListener {
                        Snackbar.make(
                            saveButton,
                            "Question difficulty saved successfully",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SettingActivity", "Failed to save question difficulty", e)
                        Snackbar.make(
                            saveButton,
                            "Failed to save question difficulty",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home_icon -> {
                // Navigate to the home activity (you can replace this with your target activity)
                val newIntent = Intent(this, HomeActivity::class.java) // Assuming HomeActivity is your target activity
                newIntent.putExtra("CURRENT_USER_ID", userID)
                startActivity(newIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
