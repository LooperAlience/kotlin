package chela.tutorial.common

import android.app.Application
import chela.kotlin.Ch


class App:Application(){
    companion object {
        val looper = Ch.looper()
    }
    override fun onCreate(){
        super.onCreate()
        Ch(this)
    }
}