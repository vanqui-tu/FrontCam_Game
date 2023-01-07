package com.example.frontcamgame.gamemodule.gameobject

import android.graphics.Bitmap
import com.example.mygame.gameobject.Player
import kotlin.random.Random


class BonusApple(bitmap: Array<Bitmap>,
               player: Player,
               index: Int,
               maxBonusApple: Int,
               velocity: Float = 20F): EffectObject(bitmap, player, velocity) {

    private val index = index
    private var maxBonusApple = maxBonusApple

    init {
        incrementalVelocity = 0.2F
        damageToPlayer = -10
        score = 10.0
        collideDistance = (w!! + h!!) / 2

        reset_X_Y()
    }

    override fun update() {
        y += velocity
        // indexSelected = (indexSelected + 1) % bitmap.size
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
                / maxBonusApple.toFloat()
                ).toFloat()
    }


}