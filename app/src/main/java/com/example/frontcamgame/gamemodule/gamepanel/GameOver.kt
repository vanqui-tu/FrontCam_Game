package com.example.mygame.gamepanel

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import com.example.frontcamgame.gamemodule.gamepanel.Score

class GameOver(score: Score): AppCompatActivity() {
    private var scorePlayer = score

    private final var endGame:String = "Game Over"
    private final var paint0 = Paint()
    private final var paint1 = Paint()
    private final var paint2 = Paint()
    private  final var PAINT_BACKBROUND = Color.argb(150, 0,0,0)
    private final var GAMEOVER_COLOR = Color.RED
    private final var SCORE_COLOR = Color.WHITE

    private final var PAINT_SCORE_SIZE = 100F
    private final var PAINT_GAMEOVER_SIZE = 150F

    private final var screenWidth =
        Resources.getSystem().displayMetrics.widthPixels
    private final var screenHeight =
        Resources.getSystem().displayMetrics.heightPixels
    private var dest = Rect(0, 0, screenWidth, screenHeight);

    init{
        paint0.setColor(PAINT_BACKBROUND)

        paint1.textSize = PAINT_SCORE_SIZE
        paint1.setColor(SCORE_COLOR)
        paint1.setTextAlign(Paint.Align.CENTER)
        paint1.setFilterBitmap(true)

        paint2.textSize = PAINT_GAMEOVER_SIZE
        paint2.setColor(GAMEOVER_COLOR)
        paint2.setTextAlign(Paint.Align.CENTER)
        paint2.setFilterBitmap(true)
    }

    fun draw(canvas: Canvas){
        val locationX =
            canvas.width.toFloat() / 2
        val locationY =
            canvas.height / 2 - (paint1.descent() + paint1.ascent())/2
        canvas.drawRect(dest, paint0)
        canvas.drawText("Your score: " + scorePlayer.getScore(),
            locationX, locationY - locationY / 2, paint1)
        canvas.drawText(endGame, locationX, locationY, paint2)
    }



}