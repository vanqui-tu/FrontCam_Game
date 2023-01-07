package com.example.mygame.gamepanel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.frontcamgame.gamemodule.GameThread



class Perfomance(thread: GameThread) {
    private final var thread = thread
    private var avgFPS: Double = 0.0
    private var paint = Paint();

    init {
        paint.setColor(Color.WHITE)
        paint.setTextSize(50.0F)
    }

    fun draw(canvas: Canvas){
        drawFPS(canvas)
    }

    fun drawFPS(canvas: Canvas){
        avgFPS = thread.getAverageFPS()
        canvas.drawText("FPS: " + avgFPS.toString(),
                        150F, 250F, paint)
    }
}