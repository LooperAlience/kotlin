package chela.tutorial.src1.holder

import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.common.Holder
import chela.tutorial.common.Scene
import chela.tutorial.src1.viewmodel.SplashVM

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
                render()
            }
        }
    }
}