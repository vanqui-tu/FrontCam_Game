package com.example.mygame.gameobject

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas

abstract class GameObject(var bitmap: Array<Bitmap>) {
    var x: Float = 0.0F
    var y: Float = 0.0F
    protected var velocityX: Float = 0F
    protected var velocityY: Float = 0F

    final var w: Int? = null
    final var h: Int? = null

    protected final var screenWidth =
        Resources.getSystem().displayMetrics.widthPixels
    protected final var screenHeight =
        Resources.getSystem().displayMetrics.heightPixels
    protected var indexSelected: Int = 0

    init {
        w = bitmap[0].width
        h = bitmap[0].height
    }

    open fun draw(canvas: Canvas){
        canvas.drawBitmap(bitmap[indexSelected],
            x, y, null)
    }


    abstract fun update();
    fun getDistanceBetweenObjects(other_obj: GameObject): Int {
        return Math.sqrt(
            Math.pow((x + this.w!! / 2 - (other_obj.x + other_obj.w!! / 2)).toDouble(), 2.0) +
            Math.pow((y + this.h!! / 2 - (other_obj.y + other_obj.h!! / 2)).toDouble(), 2.0)
        ).toInt()
    }
}