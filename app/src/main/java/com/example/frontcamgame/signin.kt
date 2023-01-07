package com.example.frontcamgame

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*


class signin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true
    var callbackManager = CallbackManager.Factory.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("signin's oncreate", "called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        auth = Firebase.auth
        setupButtons()
        if (auth.currentUser != null) {
            var intent: Intent = Intent(this@signin, home::class.java)
            startActivity(intent)
        }

        setupFacebookLogin()
    }

    fun setupButtons() {
        val et_email = findViewById<EditText>(R.id.signin_email)
        val et_pw = findViewById<EditText>(R.id.signin_pw)
        val signin_btn = findViewById<AppCompatButton>(R.id.signin_btn)
        signin_btn.setOnClickListener {
            val email: String = et_email.text.toString()
            val pw: String = et_pw.text.toString()
            Log.d("Authentication", "processing" + email + pw)
            if (email != null && pw != null) {
                if (email.length > 0 && pw.length > 0) {
                    auth.signInWithEmailAndPassword(email, pw).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("Authentication", "signed in")
                            Toast.makeText(this@signin, "Authentication failed", Toast.LENGTH_SHORT)
                            val intent = Intent(this, home::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Log.d("Authentication", "sign in failed")
                            Toast.makeText(this@signin, "Authentication failed", Toast.LENGTH_SHORT)
                        }
                    }
                }
            }
        }

        val to_signup_btn = findViewById<AppCompatButton>(R.id.tosignup_btn)
        to_signup_btn.setOnClickListener {
            val intent = Intent(this@signin, signup::class.java)
            startActivity(intent)
        }


    }

    fun setupFacebookLogin() {
        val facebook_login_btn = findViewById<AppCompatButton>(R.id.fb_login_btn)
        facebook_login_btn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
            LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("Facebook login", "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d("Facebook login", "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d("Facebook login", "facebook:onError", error)
                }
            })
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("Facebook auth", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Facebook auth", "signInWithCredential:success")
                    val user = auth.currentUser
                    var intent = Intent(this@signin, home::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Facebook auth", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}



























