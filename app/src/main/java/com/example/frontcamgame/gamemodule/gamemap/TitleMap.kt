package com.example.frontcamgame.gamemodule.gamemap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.example.mygame.gameobject.GameObject

class TitleMap(bitmap: Array<Bitmap>): GameObject(bitmap) {
    private var paint = Paint()
    private var dest = Rect(0, 0, screenWidth, screenHeight);

    init {
        paint.setFilterBitmap(true)
    }

    override fun update() {}

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(bitmap[indexSelected]!!, null, dest, paint)
    }
}