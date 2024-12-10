package com.example.cwquizapp


import android.util.Log
import android.content.Context
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import org.json.JSONObject

data class TriviaQuestion(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val questionType: String
)

private fun fetchTriviaQuestions(
    context: Context,  // Added Context parameter
    catTag: Int,
    callback: (Array<TriviaQuestion>) -> Unit
) {
    val url = "https://opentdb.com/api.php?amount=10&category=$catTag"

    Ion.with(context)  // Use the passed context
        .load(url)
        .asString()
        .setCallback { e, result ->
            if (e != null) {
                e.printStackTrace()
                Log.e("QuestionActivity", "Error fetching questions")
                callback(emptyArray()) // Return an empty array on error
                return@setCallback
            }

            try {
                // Parse JSON Response
                val json = JSONObject(result)
                val questionsArray = json.getJSONArray("results")
                val questionsList = mutableListOf<TriviaQuestion>()

                if (questionsArray.length() == 0) {
                    Log.w("QuestionActivity", "No questions available")
                    callback(emptyArray()) // Return empty if no questions are found
                    return@setCallback
                }

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
                    questionsList.add(triviaQuestion)
                }

                // Return the array of questions via callback
                callback(questionsList.toTypedArray())
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("QuestionActivity", "Error parsing JSON")
                callback(emptyArray()) // Return an empty array on error
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
