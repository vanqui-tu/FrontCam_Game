package com.example.mygame.gameobject

import android.graphics.Bitmap
import com.example.frontcamgame.gameobject.EffectObject

class Ghost(bitmap: Array<Bitmap>,
            player: Player,
            _velocity: Float = 30F): EffectObject(bitmap, player, _velocity) {
    private var slope: Double = 0.0

    init {
        incrementalVelocity = .3F
        damageToPlayer = 10
        collideDistance = (w!! + h!!) / 2

        calculateVX_VY(velocity)
    }

    override fun update() {
        x += velocityX
        y += velocityY
        if(x < 0 || x.toInt() >= screenWidth ||
            y.toInt() > screenHeight){
           reset()
        }
        isCollided()
    }

    private fun calculateVX_VY(veloc: Float){
        x = (0..screenWidth).random().toFloat()
        y = -(1..10).random() * w!!.toFloat()

        slope = (player.y - y).toDouble() / (player.x - x).toDouble()
        if(Math.abs(slope) < 1E-5){
            velocityX = 0F
            velocityY = veloc
        }
        else{
            // Độ dốc nhỏ, Dx > Dy
            if(Math.abs(slope) < 1){
                velocityX = if(slope < 0) -veloc else veloc
                velocityY = Math.abs(veloc * slope.toInt())
            }
            else{
                velocityX = (veloc / slope).toFloat()
                velocityY = veloc
            }
        }
    }

    override fun reset(){
        velocity += incrementalVelocity
        indexSelected = (0..bitmap.size - 1).random()
        calculateVX_VY(velocity)
    }


}