package com.example.frontcamgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var signup_back_btn: AppCompatButton
    private lateinit var signup_btn: AppCompatButton
    private lateinit var et_email:EditText
    private lateinit var et_pw: EditText
    private lateinit var et_repw: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = Firebase.auth
        setupViews()
        setupOnClicks()
    }

    private fun setupOnClicks() {
        signup_back_btn.setOnClickListener { finish() }
        signup_btn.setOnClickListener { signUp() }
    }

    private fun setupViews() {
        signup_back_btn = findViewById<AppCompatButton>(R.id.signup_back_btn)
        et_email = findViewById<EditText>(R.id.signup_email)
        et_pw = findViewById<EditText>(R.id.signup_pw)
        et_repw = findViewById(R.id.signup_repw)
        signup_btn = findViewById<AppCompatButton>(R.id.signup_btn)
    }

    private fun signUp() {
        val email = et_email.text.toString()
        val pw = et_pw.text.toString()
        val repw = et_repw.text.toString()
        if (email == null || email.length == 0)
        {
            Toast.makeText(this@signup, "Email can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pw == null || pw.length == 0) {
            Toast.makeText(this@signup, "Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (repw == null || repw.length == 0) {
            Toast.makeText(this@signup, "Re-password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (pw != repw) {
            Toast.makeText(this@signup, "Re-password does not match", Toast.LENGTH_SHORT).show()
            return
        }


        try {
            auth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("Authentication", "User account created")
                    Toast.makeText(this@signup, "User account created", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Log.d("Authentication", "Sign up failed")
                    Toast.makeText(this@signup, "Sign up failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener(this) {
                Toast.makeText(this@signup, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@signup, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}