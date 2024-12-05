package com.example.cwquizapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyApdapter (private val imageModelArrayList: MutableList<MyModel>) : RecyclerView.Adapter<MyApdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyApdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val myView = inflater.inflate(R.layout.list_row_layout, parent, false)

        return ViewHolder(myView)
    }

    override fun onBindViewHolder(holder: MyApdapter.ViewHolder, position: Int) {
        val info = imageModelArrayList[position]
        holder.imgView.setImageResource(info.getImages())
        holder.textMsg.text = info.getNames()

        // Set the click listener for each item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, QuestionActivity::class.java) // Replace NewActivity with your target activity
            intent.putExtra("item_name", info.getNames()) // Pass data to the new activity
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