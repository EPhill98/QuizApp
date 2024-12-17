package com.example.cwquizapp

class MyModelQuestionHist {

    // Nullable properties for question, correctAnswer, and userAnswer
    private var question: String = ""
    private var correctAnswer: String = ""
    private var userAnswer: String = ""

    fun getQuestions(): String {
        return question
    }

    fun setQuestions(question: String) {
        this.question = question
    }
    fun getCorrectAnswers(): String {
        return correctAnswer
    }

    fun setCorrectAnswers(correctAnswer: String) {
        this.correctAnswer = correctAnswer
    }
    fun getUserAnswers(): String {
        return userAnswer
    }

    fun setUserAnswers(userAnswer: String) {
        this.userAnswer = userAnswer
    }
}
