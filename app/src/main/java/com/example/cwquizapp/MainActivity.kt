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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue

class MainActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var emailTxt: EditText
    private lateinit var pwTxt: EditText
    private lateinit var loginBtn: Button
    private lateinit var regBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var nextBtn: Button

    // Firebase
    private val myAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser = myAuth.currentUser

    // Tag for logging
    private val logCatTag = "MainActivity123"

    // Firebase Auth State Listener
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(logCatTag, "in onCreate")
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        emailTxt = findViewById(R.id.emailTxt)
        pwTxt = findViewById(R.id.passwordTxt)
        regBtn = findViewById(R.id.registerBtn)
        loginBtn = findViewById(R.id.loginBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        nextBtn = findViewById(R.id.nextScreenBtn)

        // Set click listeners
        loginBtn.setOnClickListener { loginClick() }
        regBtn.setOnClickListener { regClick(it) }
        logoutBtn.setOnClickListener { logoutClick() }
        nextBtn.setOnClickListener { nextScreen() }

        // Initialize Firebase AuthStateListener
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
            update()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(logCatTag, "in onStart")
        myAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        Log.i(logCatTag, "in onStop")
        myAuth.removeAuthStateListener(authStateListener)
    }

    private fun nextScreen() {
        if (currentUser != null) {
            val newIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("CURRENT_USER_ID", currentUser?.uid)
            }
            startActivity(newIntent)
        } else {
            displayMsg(nextBtn, "No user logged in!")
        }
    }

    private fun logoutClick() {
        Log.i(logCatTag, "Logout Clicked")
        myAuth.signOut()
        currentUser = null
        update()
    }

    private fun regClick(view: View) {
        Log.i(logCatTag, "Registration Clicked")

        val email = emailTxt.text.toString()
        val password = pwTxt.text.toString()

        if (!isValidInput(email, password, view)) return

        if (currentUser != null) {
            displayMsg(view, "Please logout first")
            return
        }

        myAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    closeKeyBoard()
                    update()
                } else {
                    task.exception?.let {
                        Log.e(logCatTag, "Registration Failed: ${it.localizedMessage}")
                    }
                    displayMsg(view, "Registration Failed")
                }
            }
    }

    private fun loginClick() {
        Log.i(logCatTag, "Login Clicked")

        val email = emailTxt.text.toString()
        val password = pwTxt.text.toString()

        if (!isValidInput(email, password, loginBtn)) return

        myAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    closeKeyBoard()
                    update()
                    saveUserDataToDatabase()
                } else {
                    task.exception?.let {
                        Log.e(logCatTag, "Login Failed: ${it.localizedMessage}")
                    }
                    displayMsg(loginBtn, "Login Failed")
                }
            }
    }

    private fun isValidInput(email: String, password: String, view: View): Boolean {
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            displayMsg(view, "Please enter a valid email")
            return false
        }

        if (password.isBlank() || password.length < 6) {
            displayMsg(view, "Password must be at least 6 characters")
            return false
        }

        return true
    }

    private fun saveUserDataToDatabase() {
        val userId = currentUser?.uid ?: return
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        usersRef.get().addOnSuccessListener { dataSnapshot ->
            val oldLoginTime = dataSnapshot.child(userId).child("newLogin").getValue(Long::class.java)
            val userData = mapOf(
                "newLogin" to ServerValue.TIMESTAMP,
                "email" to currentUser?.email,
                "oldLogin" to oldLoginTime
            )

            usersRef.child(userId).setValue(userData)
                .addOnSuccessListener {
                    Log.i(logCatTag, "User data updated successfully")
                }
                .addOnFailureListener { exception ->
                    Log.e(logCatTag, "Error updating user data: ${exception.localizedMessage}")
                }
        }
    }

    private fun update() {
        Log.i(logCatTag, "Updating UI")
        val greetingSpace = findViewById<TextView>(R.id.greetingSpace)
        greetingSpace.text = currentUser?.email?.let {
            getString(R.string.logged_in, it)
        } ?: getString(R.string.greeting_text)
        if (currentUser != null) {
            nextBtn.visibility = View.VISIBLE
        } else {
            nextBtn.visibility = View.GONE
        }
    }

    private fun closeKeyBoard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun displayMsg(view: View, msgTxt: String) {
        Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT).show()
    }
}
