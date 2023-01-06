package com.example.frontcamgame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import com.example.frontcamgame.gamemap.TitleMap
import com.example.frontcamgame.gameobject.BadApple
import com.example.frontcamgame.gameobject.BonusApple
import com.example.mygame.gameobject.Barrier
import com.example.mygame.gameobject.Ghost
import com.example.mygame.gameobject.Player
import com.example.mygame.gamepanel.GameOver
import com.example.mygame.gamepanel.Perfomance
import kotlinx.android.synthetic.main.activity_vision_live_preview.view.*

class GameView(context: Context, attributes: AttributeSet): SurfaceView(context, attributes), SurfaceHolder.Callback{
    private var thread: GameThread

    // Bitmap
    private final val MAX_BARRIERS_ON_SCREEN = 3;
    private final val MAX_GHOSTS_ON_SCREEN = 4;
    private final val MAX_BAD_APPLES_ON_SCREEN = 2;
    private final val MAX_BONUS_APPLES_ON_SCREEN = 2;

    private final var player_bitmaps = emptyArray<Bitmap>()
    private final var barrier_bitmaps = emptyArray<Bitmap>()
    private final var ghost_bitmaps = emptyArray<Bitmap>()
    private final var badapple_bitmaps = emptyArray<Bitmap>()
    private final var bonusapple_bitmaps = emptyArray<Bitmap>()

    private final var map_bitmaps = emptyArray<Bitmap>()

    // Objects
    private var player: Player? = null
    private var barriers = emptyArray<Barrier>()
    private var ghosts = emptyArray<Ghost>()
    private var badApples = emptyArray<BadApple>()
    private var bonusApples = emptyArray<BonusApple>()
    private var map: TitleMap? = null

    private var perfomance: Perfomance? = null
    private var gameOver = false

    init{
        // add callback
        holder.addCallback(this)
        // instantiate the game thread
        thread = GameThread(holder, this)

        // CREATE BITMAP
        createBitmaps()

        resetAll()

        // GAME PANEL
        perfomance = Perfomance(thread)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d("GameView.kt", "SurfaceCreate()")
        // Start game thread
        thread.startLoop()
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d("GameView.kt", "SurfaceChanged()")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d("GameView.kt", "SurfaceDestroyed()")
        var retry = true
        while (retry) {
            thread.stopLoop()
            retry = false
        }
    }

    fun update(){
        if(player!!.getHealthPercentage() == 0){
            gameOver = true
            return
        }

        // update BadApples
        for(i in 0 .. MAX_BAD_APPLES_ON_SCREEN-1)
            badApples[i].update()

        // update BonusApples
        for(i in 0 .. MAX_BONUS_APPLES_ON_SCREEN-1)
            bonusApples[i].update()

        // update Barriers
        for(i in 0 .. MAX_BARRIERS_ON_SCREEN-1)
            barriers[i].update()

        // Update Ghosts
        for(i in 0 .. MAX_GHOSTS_ON_SCREEN-1)
            ghosts[i].update()
        // Update Player
        player!!.update()
    }

    override fun draw(canvas: Canvas){
        super.draw(canvas)
        // cái nào vẽ trc thì nằm ở dưới

        map!!.draw(canvas)

        // Draw BadApples
        for(i in 0 .. MAX_BAD_APPLES_ON_SCREEN-1)
            badApples[i].draw(canvas)

        // draw BonusApples
        for(i in 0 .. MAX_BONUS_APPLES_ON_SCREEN-1)
            bonusApples[i].draw(canvas)

        // Draw barriers
        for(i in 0..MAX_BARRIERS_ON_SCREEN-1){
            barriers[i].draw(canvas)
        }

        // Draw barriers
        for(i in 0..MAX_GHOSTS_ON_SCREEN-1){
            ghosts[i].draw(canvas)
        }

        perfomance!!.draw(canvas)

        // Draw player (main character)
        player!!.draw(canvas)

    }
     fun onPause(){
         thread.stopLoop()
     }

    fun updatePlayer(x: Float, y: Float){
        player!!.updateByMovement(x, y);
    }

    fun resetAll(){
        gameOver = false
        // GAME OBJECT

        // Player
        player = Player(player_bitmaps)

        // Map
        map = TitleMap(map_bitmaps)

        // Bad Apple
        badApples = emptyArray()
        for (i in 0..MAX_BAD_APPLES_ON_SCREEN-1){
            badApples += BadApple(
                badapple_bitmaps,
                player!!,
                i,
                MAX_BAD_APPLES_ON_SCREEN
            )
        }

        // Bonus Apple
        bonusApples = emptyArray()
        for (i in 0..MAX_BONUS_APPLES_ON_SCREEN-1){
            bonusApples += BonusApple(
                bonusapple_bitmaps,
                player!!,
                i,
                MAX_BONUS_APPLES_ON_SCREEN
            )
        }

        // Barrier
        barriers = emptyArray()
        for (i in 0..MAX_BARRIERS_ON_SCREEN-1){
            barriers += Barrier(
                barrier_bitmaps,
                player!!,
                i,
                MAX_BARRIERS_ON_SCREEN
            )
        }

        // Ghost
        ghosts = emptyArray()
        for (i in 0..MAX_GHOSTS_ON_SCREEN-1){
            ghosts += Ghost(
                ghost_bitmaps,
                player!!,
                (20..30).random().toFloat()
            )
        }
    }

    fun over(): Boolean{
        return gameOver
    }

    fun createBitmaps(){
        player_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.player_0),
            BitmapFactory.decodeResource(resources, R.drawable.player_1),
            BitmapFactory.decodeResource(resources, R.drawable.player_2),
            BitmapFactory.decodeResource(resources, R.drawable.player_3),
            BitmapFactory.decodeResource(resources, R.drawable.player_4),
            BitmapFactory.decodeResource(resources, R.drawable.player_5),
            BitmapFactory.decodeResource(resources, R.drawable.player_7),
            BitmapFactory.decodeResource(resources, R.drawable.player_8),
            BitmapFactory.decodeResource(resources, R.drawable.player_9),
            BitmapFactory.decodeResource(resources, R.drawable.player_10),
            BitmapFactory.decodeResource(resources, R.drawable.player_11),
            BitmapFactory.decodeResource(resources, R.drawable.player_12),
            BitmapFactory.decodeResource(resources, R.drawable.player_13),
            BitmapFactory.decodeResource(resources, R.drawable.player_14),
            BitmapFactory.decodeResource(resources, R.drawable.player_15),
            BitmapFactory.decodeResource(resources, R.drawable.player_16),
            BitmapFactory.decodeResource(resources, R.drawable.player_17),
            BitmapFactory.decodeResource(resources, R.drawable.player_18),
            BitmapFactory.decodeResource(resources, R.drawable.player_19),
            BitmapFactory.decodeResource(resources, R.drawable.player_20),
            BitmapFactory.decodeResource(resources, R.drawable.player_21),
            BitmapFactory.decodeResource(resources, R.drawable.player_22),
            BitmapFactory.decodeResource(resources, R.drawable.player_23),
            BitmapFactory.decodeResource(resources, R.drawable.player_24),
            BitmapFactory.decodeResource(resources, R.drawable.player_25),
            BitmapFactory.decodeResource(resources, R.drawable.player_26),
            BitmapFactory.decodeResource(resources, R.drawable.player_27),
            BitmapFactory.decodeResource(resources, R.drawable.player_28),
            BitmapFactory.decodeResource(resources, R.drawable.player_29),
            BitmapFactory.decodeResource(resources, R.drawable.player_30),
        )

        barrier_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.barrier_0),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_1),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_2),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_3),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_4),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_5),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_6),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_7),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_8),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_9),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_10),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_11),
            BitmapFactory.decodeResource(resources, R.drawable.barrier_12)
        )

        ghost_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.ghost_0),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_1),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_2),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_3),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_4),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_5),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_6),
            BitmapFactory.decodeResource(resources, R.drawable.ghost_7)
        )
        badapple_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.badapple_0),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_1),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_2),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_3),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_4),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_5),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_6),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_7),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_8),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_9),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_10),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_11),
            BitmapFactory.decodeResource(resources, R.drawable.badapple_12)

        )
        bonusapple_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.apple_0)
        )

        map_bitmaps = arrayOf(
            BitmapFactory.decodeResource(resources, R.drawable.background)
        )
    }

}
