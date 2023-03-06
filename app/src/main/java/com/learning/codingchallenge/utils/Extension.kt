package com.learning.codingchallenge.utils

import android.content.Context
import android.content.Intent
import com.learning.codingchallenge.SnakeActivityUsingSurfaceView

object Extension {

    fun Context.internetIsConnected(): Boolean {
        return try {
            val command = "ping -c 1 google.com"
            Runtime.getRuntime().exec(command).waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

    fun Context.redirectionToGame(context: Context){
        startActivity(Intent(context,SnakeActivityUsingSurfaceView::class.java))
    }
}