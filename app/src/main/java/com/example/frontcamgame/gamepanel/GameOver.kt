package com.example.mygame.gamepanel

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.example.frontcamgame.gamepanel.Score

class GameOver(score: Score) {
    private var scorePlayer = score

    private final var endGame:String = "Game Over"
    private final var paint = Paint()

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
        paint.textSize = PAINT_SCORE_SIZE
        paint.setTextAlign(Paint.Align.CENTER)
        paint.setFilterBitmap(true)
    }

    fun draw(canvas: Canvas){
        val locationX =
            canvas.width.toFloat() / 2
        val locationY =
            canvas.height / 2 - (paint.descent() + paint.ascent())/2

        paint.setColor(PAINT_BACKBROUND)
        canvas.drawRect(dest, paint)

        paint.setColor(SCORE_COLOR)
        canvas.drawText("Your score: " + scorePlayer.getScore(),
            locationX, locationY - locationY / 2, paint)

        paint.setColor(GAMEOVER_COLOR)
        paint.textSize = PAINT_GAMEOVER_SIZE
        canvas.drawText(endGame, locationX, locationY, paint)
    }



}