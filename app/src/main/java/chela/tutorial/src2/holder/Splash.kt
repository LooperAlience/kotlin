package chela.tutorial.src2.holder

import android.util.Log
import chela.tutorial.R
import chela.tutorial.src2.App
import chela.tutorial.src2.viewmodel.Holder
import chela.tutorial.src2.viewmodel.SplashVM

object Splash: Scene(){
    override fun vm() = SplashVM
    override fun layout() = R.layout.activity_splash
    override fun init(){}
    override fun pushed() {
        Holder.isLock = true
        App.looper{
            time = vm().title.time
            block = {
                vm().title.pushAnimation(it)
                renderSync()
            }
            ended = {
                vm().pushed()
            }
        }
    }
}