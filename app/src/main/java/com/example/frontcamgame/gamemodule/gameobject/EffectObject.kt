package com.example.frontcamgame.gamemodule.gameobject

import android.graphics.Bitmap
import com.example.mygame.gameobject.GameObject
import com.example.mygame.gameobject.Player

// Những đối tượng gây tác động tới nhân vật

abstract class EffectObject(bitmap: Array<Bitmap>,
                            player: Player,
                            velocity: Float): GameObject(bitmap){

    protected final var incrementalVelocity: Float = 0.0F
    protected final var damageToPlayer: Int = 0
    protected final var collideDistance: Int = 0
    protected final var score: Double = 0.0

    protected var velocity: Float = velocity
    protected var player: Player = player

    abstract protected fun reset()

    protected fun isCollided(){
        if(this.getDistanceBetweenObjects(player) <= collideDistance){
            player.getDamaged(damageToPlayer)
            player.setBonusScore(score)
            reset()
        }
    }

    protected fun inflictDamage(){
        player.getDamaged(damageToPlayer)
    }

    protected fun impactScore(){
        player.setBonusScore(score)
    }
}
