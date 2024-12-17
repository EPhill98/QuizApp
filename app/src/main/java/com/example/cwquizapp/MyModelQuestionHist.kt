package com.example.cwquizapp

class MyModelQuestionHist {

    // Nullable properties for question, correctAnswer, and userAnswer
    var question: String = ""
    var correctAnswer: String = ""
    var userAnswer: String = ""

    fun getQuestions(): String {
        return question.toString()
    }

    fun setQuestions(question: String) {
        this.question = question
    }
    fun getCorrectAnswers(): String {
        return correctAnswer.toString()
    }

    fun setCorrectAnswers(correctAnswer: String) {
        this.correctAnswer = correctAnswer
    }
    fun getUserAnswers(): String {
        return userAnswer.toString()
    }

    fun setUserAnswers(userAnswer: String) {
        this.userAnswer = userAnswer
    }
}
