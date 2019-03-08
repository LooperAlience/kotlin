package chela.tutorial.src2

import android.app.Application
import chela.kotlin.Ch


class App:Application(){
    companion object {
        val groupBase = Ch.groupBase()
        val router = Ch.router(groupBase)
        val looper = Ch.looper()
        var isPermitted = false
    }
    override fun onCreate(){
        super.onCreate()
        Ch(this)
    }
}