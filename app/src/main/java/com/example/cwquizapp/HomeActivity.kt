package com.example.cwquizapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)

        val root = findViewById<View>(android.R.id.content)
        val currentUserEmail = intent.getStringExtra("CURRENT_USER_EMAIL")

        val welcomeSnackbar = Snackbar.make(root,"WELCOME ${currentUserEmail.toString()}", Snackbar.LENGTH_LONG)
        welcomeSnackbar.show()

        val myToolbar: Toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(myToolbar)

        val modelLst = popList()

        val recyclerView = findViewById<RecyclerView>(R.id.my_recycler_view)
        val layoutManager = LinearLayoutManager(this )
        recyclerView.layoutManager = layoutManager

        val myAdapter = MyApdapter(modelLst)
        recyclerView.adapter = myAdapter

    }

    private fun launchQScreen() {
        val newIntent = Intent(this, QuestionActivity::class.java)
        startActivity(newIntent)
    }

    private fun popList(): ArrayList<MyModel> {
        val lst = ArrayList<MyModel>()
        val myIcons = arrayOf(
            R.drawable.sports, R.drawable.globeicon, R.drawable.food, R.drawable.movie_icon,
            R.drawable.history_icon, R.drawable.science_icon, R.drawable.art_icon, R.drawable.music_icon
        )
        val myCatLst = arrayOf(
            "Sports", "Geography", "Food", "Movies",
            "History", "Science", "Art", "Music"
        )
        for (i in 0..7) {
            val imgModel = MyModel()
            imgModel.setNames((myCatLst[i]))
            imgModel.setImage(myIcons[i])
            lst.add(imgModel)
        }
        lst.sortBy { lst -> lst.modelName}
        return lst
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate((R.menu.toolbar_layout), menu)
        return super.onCreateOptionsMenu(menu)
    }


}