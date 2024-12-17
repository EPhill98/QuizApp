package com.example.cwquizapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyApdapterQuestionHistory(
    private var questionHistoryArray: MutableList<MyModelQuestionHist>
) : RecyclerView.Adapter<MyApdapterQuestionHistory.ViewHolder>() {

    // Inflate the row layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.question_list_row, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = questionHistoryArray[position]
        holder.question.text = item.getQuestions()
        holder.correctAnswer.text = item.getCorrectAnswers()
        holder.userAnswer.text = item.getUserAnswers()
    }

    // Return the size of the data list
    override fun getItemCount(): Int = questionHistoryArray.size

    // Update the data in the adapter
    fun updateList(newList: MutableList<MyModelQuestionHist>) {
        questionHistoryArray = newList
        notifyDataSetChanged()
    }

    // Define the ViewHolder class
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.question_txt)
        val correctAnswer: TextView = itemView.findViewById(R.id.correct_answer)
        val userAnswer: TextView = itemView.findViewById(R.id.user_answer)
    }
}
