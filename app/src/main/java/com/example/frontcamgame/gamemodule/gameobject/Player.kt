package com.example.mygame.gameobject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import com.example.frontcamgame.R
import com.example.frontcamgame.gamemodule.gamepanel.Score
import com.example.mygame.gamepanel.GameOver
import com.example.mygame.gamepanel.HealthBar

// Main character

class Player(bitmap: Array<Bitmap>, context: Context): GameObject(bitmap) {
    private final var MAX_HEALTH = 100
    private final val INITIAL_SCORE = 0.0

    private var health = MAX_HEALTH
    private var healthBar: HealthBar? = HealthBar(this)

    private var score = INITIAL_SCORE
    private var scorePlayer: Score? = Score(this)
    private var gameOver: GameOver? = GameOver(scorePlayer!!)
    private var SPEED = 2

    private final var damagedSound : MediaPlayer = MediaPlayer.create(context, R.raw.explosion)
    private final var bonusSound : MediaPlayer = MediaPlayer.create(context, R.raw.bonus)


    init {
        x = (screenWidth / 2).toFloat()
        y = (screenHeight / 2).toFloat()

        healthBar!!.update()
    }

    override fun update(){
        healthBar!!.update()
        scorePlayer!!.update()
        indexSelected = (indexSelected + SPEED) % bitmap.size
    }

    fun updateByMovement(_x: Float, _y: Float) {
        x = _x - w!! / 2
        y = _y - h!! / 2
    }

    override fun draw(canvas: Canvas) {
        if(health > 0) {
            super.draw(canvas)
            scorePlayer!!.draw(canvas)
            healthBar!!.draw(canvas)
        }
        else{
            gameOver!!.draw(canvas)
        }
    }

    fun getHealthPercentage(): Int{
        //Log.d("Health bar", health.toString())
        return health * 100 / MAX_HEALTH
    }

    fun getDamaged(damage: Int){
        health = Math.max(Math.min(health - damage, 100), 0)
        if (damage > 0) { damagedSound.start() }
    }

    fun getBonusScore(): Double{
        return score
    }

    fun getScore(): Int{
        return scorePlayer!!.getScore().toInt()
    }

    fun setBonusScore(bonus: Double){
        score = bonus
        scorePlayer!!.update()
        score = 0.0
        if (bonus > 0) { bonusSound.start() }
    }

}