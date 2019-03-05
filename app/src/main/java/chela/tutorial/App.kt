package chela.tutorial

import android.app.Application
import chela.kotlin.Ch


class App:Application(){
    companion object {
        val groupBase = Ch.groupBase()
        val router = Ch.router(groupBase)
        val looper = Ch.looper()
    }
    override fun onCreate(){
        super.onCreate()
        Ch(this)
    }
}