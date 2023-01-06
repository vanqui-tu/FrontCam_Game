package com.example.frontcamgame.gameobject

import android.graphics.Bitmap
import android.graphics.Canvas
import com.example.mygame.gameobject.Player
import kotlin.random.Random

class BadApple(bitmap: Array<Bitmap>,
               player: Player,
               index: Int,
               maxBadApple: Int,
               velocity: Float = 30F): EffectObject(bitmap, player, velocity) {

    private val index = index
    private var maxBadApple = maxBadApple

    init {
        incrementalVelocity = 0.25F
        damageToPlayer = 15
        score = -10.0
        collideDistance = w!! + h!!

        reset_X_Y()
    }

    override fun update() {
        y += velocity
        indexSelected = (indexSelected + 1) % bitmap.size
        if(y >= screenHeight){
            reset()
        }
        isCollided()
    }

    override fun reset() {
        velocity += incrementalVelocity
        reset_X_Y()
    }

    private fun reset_X_Y(){
        y = -1 * (2..10).random() * h!!.toFloat()
        x = ((index.toFloat() + Random.nextDouble(.3, .6)) *
                screenWidth.toFloat()
                / maxBadApple.toFloat()
                ).toFloat()
    }


}