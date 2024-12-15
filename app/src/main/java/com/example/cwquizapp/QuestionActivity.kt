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

class QuestionActivity : AppCompatActivity() {

    private val questionsList = mutableListOf<TriviaQuestion>()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample_question_activity)

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val catName = intent.getStringExtra("item_name").toString()
        val catTag = intent.getIntExtra("cat_tag", 0)
        val userId = intent.getStringExtra("user_id").toString()
        val questionNum = intent.getIntExtra("questionNum", 1)

        val catTxt = findViewById<TextView>(R.id.cat_txt)
        catTxt.text = catName

        val choiceTxt = findViewById<TextView>(R.id.choice_txt)

        val btn1 = findViewById<Button>(R.id.user_choice1)
        val btn2 = findViewById<Button>(R.id.user_choice2)
        val btn3 = findViewById<Button>(R.id.user_choice3)
        val btn4 = findViewById<Button>(R.id.user_choice4)
        val nextButton: Button = findViewById(R.id.next_button)

        val buttons = listOf(btn1, btn2, btn3, btn4)

        buttons.forEach { it.visibility = View.GONE }

        fetchTriviaQuestions(catTag, questionNum) { triviaQuestions ->
            if (triviaQuestions.isNotEmpty()) {
                questionsList.clear()
                questionsList.addAll(triviaQuestions)
                currentQuestionIndex = 0
                correctAnswers = 0
                showQuestion(questionsList[currentQuestionIndex])
            } else {
                Log.e("QuestionActivity", "No questions fetched")
            }
        }

        nextButton.setOnClickListener {
            if (currentQuestionIndex < questionsList.size - 1) {
                currentQuestionIndex++
                nextButton.visibility = View.GONE
                buttons.forEach {
                    it.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.DefaultButtonColor
                        )
                    )
                }
                choiceTxt.text = getString(R.string.choice_txt)
                showQuestion(questionsList[currentQuestionIndex])
            } else {
                choiceTxt.text =
                    "End of questions. Your score is $correctAnswers out of ${questionsList.size}"
                Log.d("QuestionActivity", "End of questions")
                nextButton.visibility = View.GONE


                val userStats = FirebaseDatabase.getInstance().getReference("userStats")
                val userRef = userStats.child(userId)

                // Fetch the current values for "correctAnswers" and the specific category
                userRef.get().addOnSuccessListener { dataSnapshot ->
                    val currentCorrectAnswers = dataSnapshot.child("correctAnswers").getValue(Int::class.java) ?: 0
                    val currentCatAnswers = dataSnapshot.child(catName).getValue(Int::class.java) ?: 0
                    val totalQuestionsAttempted = dataSnapshot.child("totalQuestions").getValue(Int::class.java) ?: 0

                    // Update "lastCategory", "correctAnswers", and category-specific answers
                    val updates = mapOf(
                        "lastCategory" to catName,
                        "correctAnswers" to (correctAnswers + currentCorrectAnswers),
                        catName to (correctAnswers + currentCatAnswers),
                        "totalQuestions" to (questionsList.size + totalQuestionsAttempted)
                    )

                    userRef.updateChildren(updates)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Data successfully updated")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firebase", "Failed to update data", exception)
                        }
                }.addOnFailureListener { exception ->
                    Log.e("Firebase", "Failed to fetch data", exception)
                }

            }
        }

        buttons.forEachIndexed { index, button ->
            nextButton.visibility = View.GONE
            choiceTxt.text = getString(R.string.choice_txt)
            button.setOnClickListener {
                val selectedAnswer = button.text.toString()
                if (selectedAnswer == questionsList[currentQuestionIndex].correctAnswer) {
                    correctAnswers++
                    choiceTxt.text = "Correct"
                } else {
                    choiceTxt.text = "Incorrect"
                }
                val userQuestionHistory = FirebaseDatabase.getInstance().getReference("userQuestionHistory")
                val userQuestHistoryRef = userQuestionHistory.child(userId)
                val updateQuestionHistory = mapOf(
                    "question" to questionsList[currentQuestionIndex].question,
                    "answer" to selectedAnswer,
                    "correctAnswer" to questionsList[currentQuestionIndex].correctAnswer
                )
                userQuestHistoryRef.push().setValue(updateQuestionHistory)

                nextButton.visibility = View.VISIBLE
                buttons.forEach { it.visibility = View.GONE }
            }
        }
    }

    private fun showQuestion(question: TriviaQuestion) {
        val questionTxt = findViewById<TextView>(R.id.question_txt)
        questionTxt.text = question.question

        val btn1 = findViewById<Button>(R.id.user_choice1)
        val btn2 = findViewById<Button>(R.id.user_choice2)
        val btn3 = findViewById<Button>(R.id.user_choice3)
        val btn4 = findViewById<Button>(R.id.user_choice4)

        val buttons = listOf(btn1, btn2, btn3, btn4)

        val answers = mutableListOf<String>().apply {
            addAll(question.incorrectAnswers ?: emptyList())
            question.correctAnswer?.let { add(it) }
            shuffle()
        }

        buttons.forEachIndexed { index, button ->
            if (index < answers.size) {
                button.text = answers[index]
                button.visibility = View.VISIBLE
            } else {
                button.visibility = View.GONE
            }
        }
    }

    private fun fetchTriviaQuestions(catTag: Int, questionNum: Int, callback: (List<TriviaQuestion>) -> Unit) {
        val url = "https://opentdb.com/api.php?amount=$questionNum&category=$catTag"
        Ion.with(this)
            .load(url)
            .asString()
            .setCallback { e, result ->
                if (e != null) {
                    e.printStackTrace()
                    Log.e("QuestionActivity", "Error fetching questions")
                    callback(emptyList())
                    return@setCallback
                }
                try {
                    val json = JSONObject(result)
                    val questionsArray = json.getJSONArray("results")
                    val questionsLst = mutableListOf<TriviaQuestion>()

                    for (i in 0 until questionsArray.length()) {
                        val questionObj = questionsArray.getJSONObject(i)
                        val question = questionObj.getString("question")
                        val correctAnswer = questionObj.getString("correct_answer")
                        val incorrectAnswers = questionObj.getJSONArray("incorrect_answers")
                        val questionType = questionObj.getString("type")

                        val incorrectAnswersList = jsonArrayToList(incorrectAnswers)

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
                    callback(emptyList())
                }
            }
    }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val myView = findViewById<View>(R.id.main_toolbar)
        when (item.itemId) {
            R.id.home_icon -> {
                finish()
                return true
            }
        }
        return true
    }
}
