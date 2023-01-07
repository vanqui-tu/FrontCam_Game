/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.frontcamgame.kotlin

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Button
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.frontcamgame.*
import com.example.frontcamgame.gamemodule.GameView
import com.google.android.gms.common.annotation.KeepName
import com.example.frontcamgame.kotlin.facedetector.FaceDetectorProcessor
import com.example.frontcamgame.models.Attempt
import com.example.frontcamgame.models.getAttempt
import com.example.frontcamgame.preference.PreferenceUtils
import com.facebook.share.model.ShareHashtag
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.IOException

/** Live preview demo for ML Kit APIs. */
@KeepName
class LivePreviewActivity :
  AppCompatActivity(), OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

  private var cameraSource: CameraSource? = null
  private var preview: CameraSourcePreview? = null
  private var graphicOverlay: GraphicOverlay? = null
  private var selectedModel = FACE_DETECTION
  private var gameView: GameView? = null //

  private var playAgainBtn: Button? = null
  private var homeBtn: Button? = null
  private var shareBtn: ImageView? = null
  private var gameOverView: RelativeLayout? = null

  private lateinit var db: FirebaseFirestore
  private lateinit var auth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "onCreate")
    setContentView(R.layout.activity_vision_live_preview)

    db = Firebase.firestore
    auth = Firebase.auth

    preview = findViewById(R.id.preview_view) //

    gameView = findViewById(R.id.game_view)
    if (preview == null) {
      Log.d(TAG, "Preview is null")
    }

    graphicOverlay = findViewById(R.id.graphic_overlay)
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null")
    }


    gameOverView = findViewById<RelativeLayout>(R.id.game_over_layout)
    playAgainBtn = findViewById(R.id.playAgainBtn)
    playAgainBtn!!.setOnClickListener {
      gameView!!.resetAll()
      gameOverView!!.visibility = if (gameOverView!!.visibility == View.INVISIBLE)
                                  View.VISIBLE
                                  else View.INVISIBLE
    }

    homeBtn = findViewById(R.id.homeBtn)
    homeBtn!!.setOnClickListener {
      // intent to home
      finish()
      gameOverView!!.visibility = if (gameOverView!!.visibility == View.INVISIBLE)
                                    View.VISIBLE
                                    else View.INVISIBLE
    }

    shareBtn = findViewById(R.id.shareResult)
    shareBtn!!.setOnClickListener{
      screen_share()
    }
    gameView!!.getViews(gameOverView)
    gameView!!.gameover_callback = ::callback
  }

  fun callback() {
    runBlocking {
      launch {
        add_score()
      }
    }
    gameOverView!!.visibility = if (gameOverView!!.visibility != View.VISIBLE)
                                  View.VISIBLE
                                else View.INVISIBLE
  }

  @Synchronized
  override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent?.getItemAtPosition(pos).toString()
    Log.d(TAG, "Selected model: $selectedModel")
    preview?.stop()
    createCameraSource(selectedModel)
    startCameraSource()
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
    // Do nothing.
  }

  override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
    Log.d(TAG, "Set facing")
//    if (cameraSource != null) {
//      if (isChecked) {
//        cameraSource?.setFacing(CameraSource.CAMERA_FACING_FRONT)
//      } else {
//        cameraSource?.setFacing(CameraSource.CAMERA_FACING_BACK)
//      }
//    }

    preview?.stop()
    startCameraSource()
  }

  private fun createCameraSource(model: String) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = CameraSource(this, graphicOverlay)
    }
    try {
      when (model) {

        FACE_DETECTION -> {
          Log.i(TAG, "Using Face Detector Processor")
          val faceDetectorOptions = PreferenceUtils.getFaceDetectorOptions(this)
          cameraSource!!.setMachineLearningFrameProcessor(
            FaceDetectorProcessor(this, faceDetectorOptions, gameView)
          )
        }

        else -> Log.e(TAG, "Unknown model: $model")
      }
    } catch (e: Exception) {
      Log.e(TAG, "Can not create image processor: $model", e)
      Toast.makeText(
          applicationContext,
          "Can not create image processor: " + e.message,
          Toast.LENGTH_LONG
        )
        .show()
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private fun startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null")
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null")
        }
        preview!!.start(cameraSource, graphicOverlay)
      } catch (e: IOException) {
        Log.e(TAG, "Unable to start camera source.", e)
        cameraSource!!.release()
        cameraSource = null
      }
    }
  }

  public override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume")
    createCameraSource(selectedModel)
    startCameraSource()
  }

  /** Stops the camera. */
  override fun onPause() {
    super.onPause()
    preview?.stop()
  }

  public override fun onDestroy() {
    super.onDestroy()
    if (cameraSource != null) {
      cameraSource?.release()
    }
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
    Log.d("Add score", "123")

    var score = gameView!!.getPlayScore()
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

  companion object {
    private const val FACE_DETECTION = "Face Detection"
    private const val TAG = "LivePreviewActivity"
  }
}
