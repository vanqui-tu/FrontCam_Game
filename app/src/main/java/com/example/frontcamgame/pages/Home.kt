package com.example.frontcamgame.pages

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.frontcamgame.R
import com.facebook.AccessToken
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class home : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        db = Firebase.firestore
        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }

        val tv_email = findViewById<TextView>(R.id.home_email)
        val user = Firebase.auth.currentUser
        user?.let {
            tv_email.setText(user.email)
        }

        val logout_btn = findViewById<Button>(R.id.home_logout)
        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            if (AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null) {
                LoginManager.getInstance().logOut()
            }
            finish()
        }

        val tp_highscores_btn = findViewById<Button>(R.id.highscores_btn)
        tp_highscores_btn.setOnClickListener {
            var intent = Intent(this@home, scoreboard::class.java)
            startActivity(intent)
        }

        val play_btn = findViewById<Button>(R.id.home_gamescr_btn)
        play_btn.setOnClickListener {
            var intent: Intent = Intent(this@home, com.example.frontcamgame.kotlin.LivePreviewActivity::class.java)
            startActivity(intent)
        }
    }

    //
    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission?.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "Permission granted: $permission")
            return true
        }
        Log.i(TAG, "Permission NOT granted: $permission")
        return false
    }

    companion object {
        private const val TAG = "EntryChoiceActivity"
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
    }
}