package com.example.cwquizapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragApdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity){
    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return MultiChoice()
            1 -> return TrueFalse()
        }
        return MultiChoice()
    }

    override fun getItemCount(): Int {
        return 2
    }
}