package com.example.cwquizapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class QuestionActivity : AppCompatActivity() {

    private var isMultiChoice = true  // Keep the fragment state variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_question_activity)
        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val catName = intent.getStringExtra("item_name").toString()
        val catTxt = findViewById<TextView>(R.id.cat_txt)
        catTxt.text = catName

        val trueFalseBtn = findViewById<Button>(R.id.true_false)
        val MultiBtn = findViewById<Button>(R.id.multi)

        // Load the default fragment when the activity is first created
        if (savedInstanceState == null) {
            loadFragment()  // Load the default fragment based on isMultiChoice
        }

        // Handle button clicks to change the fragment
        trueFalseBtn.setOnClickListener {
            isMultiChoice = false
            Log.i("epdp", "clicked MULTI")
            loadFragment()  // Reload the fragment based on new state
        }

        MultiBtn.setOnClickListener {
            isMultiChoice = true
            Log.i("epdp", "clicked TF")
            loadFragment()  // Reload the fragment based on new state
        }
    }

    // This method will load the correct fragment based on isMultiChoice
    private fun loadFragment() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        val fragment: Fragment = if (isMultiChoice) {
            MultiChoice()  // Show MultiChoice fragment
        } else {
            TrueFalse()  // Show TrueFalse fragment
        }

        // Replace the fragment in the container
        fragmentTransaction.replace(R.id.fragment_container_view_tag, fragment)
        fragmentTransaction.commit()  // Commit the transaction
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
