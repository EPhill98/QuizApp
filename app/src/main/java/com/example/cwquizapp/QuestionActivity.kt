package com.example.cwquizapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import org.json.JSONArray

class QuestionActivity : AppCompatActivity() {

    private var isMultiChoice = true  // Fragment state variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_question_activity)

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val catName = intent.getStringExtra("item_name").toString()
        val catTxt = findViewById<TextView>(R.id.cat_txt)
        val catTag = intent.getIntExtra("cat_tag",0)

        catTxt.text = catName

        val trueFalseBtn = findViewById<Button>(R.id.true_false)
        val multiBtn = findViewById<Button>(R.id.multi)

        // Load the default fragment when the activity is first created
        if (savedInstanceState == null) {
            loadFragment()  // Load the default fragment based on isMultiChoice
        }

        // Handle button clicks to change the fragment
        trueFalseBtn.setOnClickListener {
            isMultiChoice = false
            Log.i("QuestionActivity", "Switched to True/False")
            loadFragment()  // Reload the fragment based on new state
        }

        multiBtn.setOnClickListener {
            isMultiChoice = true
            Log.i("QuestionActivity", "Switched to Multiple Choice")
            loadFragment()  // Reload the fragment based on new state
        }

        // Fetch questions using Ion
        fetchTriviaQuestions(catTag)
    }

    // Fetch trivia questions from the API
    private fun fetchTriviaQuestions(catTag : Int) {
        val url = "https://opentdb.com/api.php?amount=10&category=$catTag"

        Ion.with(this)
            .load(url)
            .asString()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Log.e("QuestionActivity", "Error fetching questions")
                    return@setCallback
                }

                try {
                    // Parse JSON Response
                    val json = JSONObject(result)
                    val questions = json.getJSONArray("results")

                    for (i in 0 until questions.length()) {
                        val questionObj = questions.getJSONObject(i)
                        val question = questionObj.getString("question")
                        val correctAnswer = questionObj.getString("correct_answer")
                        val incorrectAnswers = questionObj.getJSONArray("incorrect_answers")

                        // Log the question and answers
                        Log.i("QuestionActivity", "Question: $question")
                        Log.i("QuestionActivity", "Correct Answer: $correctAnswer")
                        for (j in 0 until incorrectAnswers.length()) {
                            Log.i("QuestionActivity", "Incorrect Answer: ${incorrectAnswers.getString(j)}")
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.e("QuestionActivity", "Error parsing JSON")
                }
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
