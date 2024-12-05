package com.example.cwquizapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var emailTxt : EditText
    private lateinit var pwTxt : EditText
    private lateinit var loginBtn : Button
    private lateinit var regBtn : Button
    private lateinit var logoutBtn: Button
    private lateinit var nextBtn: Button


    private var logCatTag  = "epdp"

    private var myAuth = FirebaseAuth.getInstance()
    private var currentUser = myAuth.currentUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(logCatTag, "in onCreate")
        setContentView(R.layout.activity_main)

        emailTxt = findViewById(R.id.emailTxt)
        pwTxt = findViewById(R.id.passwordTxt)

        regBtn = findViewById(R.id.registerBtn)
        loginBtn = findViewById(R.id.loginBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        nextBtn = findViewById<Button>(R.id.nextScreenBtn)

        loginBtn.setOnClickListener{_ -> loginClick()}
        regBtn.setOnClickListener{v  -> regClick(v)}
        logoutBtn.setOnClickListener{_ -> logoutClick()}
        nextBtn.setOnClickListener{_ -> nextScreen()}

        update()

    }

    private fun nextScreen() {
        if (currentUser != null) {
            val currentEmail = currentUser?.email // Get the current user's email
            val newIntent = Intent(this, HomeActivity::class.java)
            newIntent.putExtra("CURRENT_USER_EMAIL", currentEmail)
            startActivity(newIntent)
        } else {
            displayMsg(nextBtn, "No user logged in!")
        }
    }

    private fun logoutClick() {
        Log.i(logCatTag, "Logout Clicked")
        currentUser = myAuth.currentUser
        myAuth.signOut()
        update()
    }

    private fun regClick(view : View) {
        Log.i(logCatTag, "Reg Clicked")
        if (myAuth.currentUser != null){
            displayMsg(view, "Please logout first")
        }
        else {
            myAuth.createUserWithEmailAndPassword(
                emailTxt.text.toString(),
                pwTxt.text.toString()
            ).addOnCompleteListener(this) {task ->
                if (task.isSuccessful ){
                    closeKeyBoard()
                    update()
                } else {
                    closeKeyBoard()
                    displayMsg(view, "Registration Failed")
                }
            }
        }
    }

    private fun loginClick() {
        Log.i(logCatTag, "Login Clicked")

        // Check if email or password is empty, and if currentUser is not null
        if ((emailTxt.text.toString().isNotEmpty() && pwTxt.text.toString().isNotEmpty()) || currentUser != null) {
            myAuth.signInWithEmailAndPassword(
                emailTxt.text.toString(),
                pwTxt.text.toString()
            ).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    closeKeyBoard()
                    update()
                } else {
                    closeKeyBoard()
                    displayMsg(loginBtn, "Login Failed")
                }
            }.addOnFailureListener(this) {
                // Handle failure case
                displayMsg(loginBtn, "Login Failed - Try Again")
            }
        } else {
            // Inform the user to fill out the fields
            displayMsg(loginBtn, "Please enter both email and password")
        }
    }

    private fun update(){
        Log.i(logCatTag, "in update")

        currentUser = myAuth.currentUser

        var currentEmail = currentUser?.email
        val greetingSpace = findViewById<TextView>(R.id.greetingSpace)
        if (currentEmail == null){
            greetingSpace.text = getString(R.string.greeting_text)
        } else {
            greetingSpace.text = getString(R.string.logged_in, currentEmail)
        }
    }

    private fun closeKeyBoard(){
        val view = this.currentFocus
        if (view != null){
            val imn = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun displayMsg(view : View, msgTxt : String){
        val snackbar = Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }


    override fun onStart(){
        super.onStart()
        Log.i(logCatTag, "in onStart")
        update()
    }

    override fun onStop(){
        super.onStop()
        Log.i(logCatTag, "in onStop")
        currentUser = myAuth.currentUser
        myAuth.signOut()
        update()
    }

}
