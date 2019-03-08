package chela.tutorial.src1.holder

import android.util.Log
import chela.tutorial.R
import chela.tutorial.src1.App
import chela.tutorial.src1.viewmodel.Holder
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
                vm().title.pushAnimation(it)
                renderSync()
            }
            ended = {
                vm().pushed()
            }
        }
    }
}