package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject

class QuestionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_question_activity)

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val catName = intent.getStringExtra("item_name").toString()
        val catTag = intent.getIntExtra("cat_tag", 0)

        val catTxt = findViewById<TextView>(R.id.cat_txt)
        catTxt.text = catName

        // Fetch questions using Ion and save them in the questions list
        fetchTriviaQuestions(catTag) { triviaQuestions ->
            val database = FirebaseDatabase.getInstance()
            val triviaQuestion = database.getReference("triviaQuestions")
            triviaQuestion.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("QuestionActivity", "Trivia questions removed successfully")
                } else {
                    Log.e(
                        "QuestionActivity",
                        "Error removing trivia questions: ${task.exception?.message}"
                    )
                }
                if (triviaQuestions.isNotEmpty()) {
                    val database = FirebaseDatabase.getInstance()
                    val triviaQuestionsRef =
                        database.getReference("triviaQuestions") // Create a reference to your trivia questions node

                    // Iterate through the trivia questions and add them to the database
                    for (triviaQuestion in triviaQuestions) {
                        val questionKey =
                            triviaQuestionsRef.push().key // Generate a unique key for each question
                        if (questionKey != null) {
                            triviaQuestionsRef.child(questionKey)
                                .setValue(triviaQuestion) // Add the question to the database
                                .addOnSuccessListener {
                                    Log.d("QuestionActivity", "Trivia question added successfully")
                                }
                                .addOnFailureListener { exception ->
                                    Log.e(
                                        "QuestionActivity",
                                        "Error adding trivia question: ${exception.message}"
                                    )
                                }
                        }
                    }
                }
            }
        }

    }

    // Fetch trivia questions from the API and pass them back via a callback
    private fun fetchTriviaQuestions(catTag: Int, callback: (List<TriviaQuestion>) -> Unit) {
        val url = "https://opentdb.com/api.php?amount=10&category=$catTag"
        Ion.with(this)
            .load(url)
            .asString()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Log.e("QuestionActivity", "Error fetching questions")
                    callback(emptyList()) // If an error occurs, return an empty list
                    return@setCallback
                }
                try {
                    // Parse JSON Response
                    val json = JSONObject(result)
                    val questionsArray = json.getJSONArray("results")
                    val questionsLst = mutableListOf<TriviaQuestion>()

                    for (i in 0 until questionsArray.length()) {
                        val questionObj = questionsArray.getJSONObject(i)
                        val question = questionObj.getString("question")
                        val correctAnswer = questionObj.getString("correct_answer")
                        val incorrectAnswers = questionObj.getJSONArray("incorrect_answers")
                        val questionType = questionObj.getString("type")

                        // Convert JSONArray to List<String>
                        val incorrectAnswersList = jsonArrayToList(incorrectAnswers)

                        // Create TriviaQuestion object and add to list
                        val triviaQuestion = TriviaQuestion(
                            question = question,
                            correctAnswer = correctAnswer,
                            incorrectAnswers = incorrectAnswersList,
                            questionType = questionType
                        )
                        questionsLst.add(triviaQuestion)
                    }

                    callback(questionsLst)

                } catch (ex: Exception) {
                    ex.printStackTrace()
                    Log.e("QuestionActivity", "Error parsing JSON")
                    callback(emptyList()) // Return an empty list in case of JSON parsing error
                }
            }
    }

    // Helper function to convert JSONArray to List<String>
    private fun jsonArrayToList(jsonArray: JSONArray): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_layout, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
