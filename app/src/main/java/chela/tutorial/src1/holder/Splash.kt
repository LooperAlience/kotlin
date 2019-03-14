package chela.tutorial.src1.holder

import android.util.Log
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.common.Holder
import chela.tutorial.common.Scene
import chela.tutorial.src1.Act
import chela.tutorial.src1.viewmodel.SplashVM

object Splash: Scene(){
    override fun vm() = SplashVM
    override fun layout() = R.layout.activity_splash
    override fun init(){}
    override fun pushed() {
        Holder.isLock = true
        Log.i("ch", "pushed")
        App.looper{
            time = vm().title.time
            block = {
                Log.i("ch", "Splash block")
                vm().title.pushAnimation(it)
                renderSync()
            }
            ended = {
                Log.i("ch", "Splash ended")
                vm().pushed()
                render()
            }
        }
    }
}