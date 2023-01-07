package com.example.frontcamgame.gamemodule.gamepanel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.mygame.gameobject.Player
import kotlin.math.roundToInt

class Score(player: Player): PlayerEffect(player) {
    private val SCORE_UPDATE = 0.2
    private var playerScore = 0.0
    private var paint = Paint()

    init {
        paint.setColor(Color.WHITE)
        paint.setTextSize(75.0F)
    }

    override fun draw(canvas: Canvas) {
        canvas.drawText("Score: " + playerScore.roundToInt().toString(),
            150F, 150F, paint)
    }

    override fun update() {
        playerScore += player!!.getBonusScore() + SCORE_UPDATE
    }

    fun getScore(): Int{
        return playerScore.toInt()
    }
}