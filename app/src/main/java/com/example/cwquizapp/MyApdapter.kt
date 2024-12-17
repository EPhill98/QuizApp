package com.example.cwquizapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class MyApdapter(private val imageModelArrayList: MutableList<MyModel>, currentUserID: String) : RecyclerView.Adapter<MyApdapter.ViewHolder>(){

    private val currentUserID = currentUserID

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyApdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val myView = inflater.inflate(R.layout.list_row_layout, parent, false)
        return ViewHolder(myView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]
        holder.imgView.setImageResource(info.getImages())
        holder.textMsg.text = info.getNames()

        // Set the click listener for each item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, QuestionActivity::class.java)
            intent.putExtra("item_name", info.getNames())
            intent.putExtra("cat_tag", info.getCatTag())
            intent.putExtra("user_id", currentUserID)

            val databaseUser = FirebaseDatabase.getInstance()
            val userStatsRef = databaseUser.getReference("userStats").child(currentUserID)

            // Only update if the category is different to prevent unnecessary writes
            userStatsRef.child("lastCategory").get().addOnSuccessListener {
                val lastCategory = it.getValue(String::class.java)
                if (lastCategory != info.getNames()) {
                    userStatsRef.child("lastCategory").setValue(info.getNames())
                        .addOnSuccessListener {
                            Log.i("MyApdapter", "Successfully updated lastCategory to ${info.getNames()}")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("MyApdapter", "Error updating lastCategory: ${exception.localizedMessage}")
                        }
                }
            }

            context.startActivity(intent)
            Log.i("epdp", "clicked CAT")
        }
    }

    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
    {
        val imgView = itemView.findViewById<View>(R.id.icon) as ImageView
        val textMsg = itemView.findViewById<View>(R.id.firstLine) as TextView
        override fun onClick(v: View?) {

        }
    }
}