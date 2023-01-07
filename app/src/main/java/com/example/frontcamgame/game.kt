package com.example.frontcamgame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class game : AppCompatActivity() {
    private lateinit var back_btn: ImageButton
    private lateinit var share_btn: ImageButton
    private lateinit var add_btn: Button
    private lateinit var score_et: EditText

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        db = Firebase.firestore
        auth = Firebase.auth
        setupViews()
        setupActions();
    }

    private fun setupViews() {
        back_btn = findViewById(R.id.game_back_btn)
        share_btn = findViewById(R.id.game_share_btn)
        add_btn = findViewById(R.id.game_add_btn)
        score_et = findViewById(R.id.game_score_et)
    }

    private fun setupActions() {
        back_btn.setOnClickListener { finish() }

        share_btn.setOnClickListener { screen_share() }

        add_btn.setOnClickListener { runBlocking { add_score() } }

    }


    // rewrite this
    private fun get_score(): Int {
        return score_et.text.toString().toInt();
    }

    private fun check_auth(): Boolean {
        return auth.currentUser != null
    }

    private suspend fun check_existed_record(): Attempt? {
        val user = auth.currentUser
        var coll = db.collection("high_score")
        var search_task = coll.whereEqualTo("uid", user!!.uid).get()
        search_task.await()

        if (search_task.isSuccessful)
            if (search_task.result.size() > 0)
                return getAttempt(search_task.result.elementAt(0))

        return null
    }

    private suspend fun add_score() {
        var score = get_score()
        score_et.text.clear()
        if (score <= 0)
            return

        if (!check_auth())
            return

        var existed_record = check_existed_record()
        Log.d("GameActivity", check_existed_record().toString())

        val user = auth.currentUser
        val data = hashMapOf(
            "uid" to user?.uid,
            "name" to (user?.displayName ?: ""),
            "email" to (user?.email ?: ""),
            "score" to score,
            "avatar" to (user?.photoUrl?.toString() ?: "")
        )
        val coll = db.collection("high_score")
        if (existed_record == null) {
            coll.add(data)
        }
        else {
            if (existed_record.score!! < score.toLong()) {
                coll.whereEqualTo("uid", existed_record.uid).get().addOnSuccessListener {
                    if (it != null && it.size() > 0)
                        it.forEach { queryDocumentSnapshot ->
                           queryDocumentSnapshot.reference.update("score", score)
                        }
                }
            }
        }

    }

    private fun screen_share() {
        val screenshot: Bitmap = takeScreenshotOfView()
        var sharePhoto: SharePhoto = SharePhoto.Builder().setBitmap(screenshot).setCaption("Caption").build()
        var shareHashtag: ShareHashtag = ShareHashtag.Builder().setHashtag("hashtag").build()
        var photoContent: SharePhotoContent = SharePhotoContent.Builder().addPhoto(sharePhoto).setShareHashtag(shareHashtag).build()
        var dialog: ShareDialog = ShareDialog(this)
        dialog.show(photoContent, ShareDialog.Mode.AUTOMATIC)
    }

    fun takeScreenshotOfView(): Bitmap {
        var v1: View = window.decorView.rootView
        v1.isDrawingCacheEnabled = true
        var res: Bitmap = Bitmap.createBitmap(v1.drawingCache)
        v1.isDrawingCacheEnabled = false
        return res
    }


}