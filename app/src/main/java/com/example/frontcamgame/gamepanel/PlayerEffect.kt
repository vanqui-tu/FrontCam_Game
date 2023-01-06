package com.example.frontcamgame.gamepanel

import android.graphics.Canvas
import com.example.mygame.gameobject.Player

abstract class PlayerEffect(player: Player) {
    protected var player = player
    protected var x: Float = 0F
    protected var y: Float = 0F
    protected var w: Int = 0
    protected var h: Int = 0

    abstract fun draw(canvas: Canvas)
    abstract fun update()
}