package com.example.cwquizapp

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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