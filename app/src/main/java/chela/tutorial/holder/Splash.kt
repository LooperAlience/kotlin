package chela.tutorial.holder

import android.util.Log
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.SplashVM

object Splash:Scene(){
    override fun vm() = SplashVM
    override fun layout() = R.layout.activity_splash
    override fun init(){}
    override fun pushed() {
        Holder.isLock = true
        Log.i("ch", "pushed")
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