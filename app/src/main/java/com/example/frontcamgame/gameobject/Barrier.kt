package com.example.mygame.gameobject

import android.graphics.Bitmap
import com.example.frontcamgame.gameobject.EffectObject
import kotlin.random.Random

class Barrier(bitmap: Array<Bitmap>,
              player: Player,
              index: Int,
              maxBarrier: Int,
              velocity: Float = 20F): EffectObject(bitmap, player, velocity)
{

    private final val index = index
    private final val maxBarrier = maxBarrier

    init {
        collideDistance = (w!! + h!!) / 6
        damageToPlayer = 5
        incrementalVelocity = .2F

        reset_X_Y()
    }

    override fun update() {
        y += velocity
        if(y >= screenHeight){
            reset()
        }
        isCollided()
    }

    override fun reset(){
        velocity += incrementalVelocity
        reset_X_Y()
        indexSelected = (0..bitmap.size - 1).random()
    }

    private fun reset_X_Y(){
        y = -1 * (2..10).random() * h!!.toFloat()
        x = ((index.toFloat() + Random.nextDouble(.3, .6)) *
                screenWidth.toFloat()
                / maxBarrier.toFloat()
                ).toFloat()
    }

}