package com.example.mygame.gamepanel

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.example.frontcamgame.gamemodule.gamepanel.PlayerEffect
import com.example.mygame.gameobject.Player

class HealthBar(player: Player): PlayerEffect(player){
    private var center_x: Float = 0F
    private var healthPercent: Int = 0

    // Left, Right, Bottom, Top
    private var fullHealthBarRec = arrayOf(0F,0F,0F,0F)
    private var healthBarRec = arrayOf(0F,0F,0F,0F)
    private final val marginBottom = 20

    private var paintFullHealth = Paint()
    private var paintHealth = Paint()

    init {
        w = player.w!!
        h = 30
        paintHealth.setColor(Color.GREEN)
        paintFullHealth.setColor(Color.GRAY)
    }

    override fun draw(canvas: Canvas){
        drawFullHeathbar(canvas)
        drawHealthbar(canvas)
    }

    override fun update(){
        x = player.x
        y = player.y
        center_x = (x + w / 2).toFloat()

        healthPercent = player.getHealthPercentage() * 2 * w / 100
        updateFullHealthbar()
        updateHealthbar()
    }

    private fun drawFullHeathbar(canvas: Canvas){
        canvas.drawRect(fullHealthBarRec[0],
            fullHealthBarRec[3],
            fullHealthBarRec[1],
            fullHealthBarRec[2],
            paintFullHealth)

    }

    private fun drawHealthbar(canvas: Canvas){
        canvas.drawRect(healthBarRec[0],
            healthBarRec[3],
            healthBarRec[1],
            healthBarRec[2],
            paintHealth)
    }

    private fun updateFullHealthbar(){
        fullHealthBarRec[0] = center_x - w
        fullHealthBarRec[1] = center_x + w
        fullHealthBarRec[2] = y - marginBottom
        fullHealthBarRec[3] = fullHealthBarRec[2] - h
    }

    private fun updateHealthbar(){
        healthBarRec[0] = fullHealthBarRec[0]
        healthBarRec[1] = healthBarRec[0] + healthPercent
        healthBarRec[2] = fullHealthBarRec[2]
        healthBarRec[3] = fullHealthBarRec[3]

        if(healthPercent >= 80)
            paintHealth.setColor(Color.GREEN)
        else if(healthPercent >= 65)
            paintHealth.setColor(Color.argb(255, 255,127,80))
        else if(healthPercent >= 50)
            paintHealth.setColor(Color.argb(255, 255,99,71))
        else
            paintHealth.setColor(Color.argb(255, 255,69,0))
    }
}