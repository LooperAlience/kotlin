package chela.tutorial.holder

import android.util.Log
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChHolderBase
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.SplashVM

object Splash:Scene(){
    @JvmStatic private val vm = SplashVM
    override fun layout() = R.layout.appsplash
    override fun resume(base: ChHolderBase<View>, isRestore: Boolean){
        if(isRestore){
            vm.resumed()
            render()
            start()
        }else{
            App.looper{
                time = Holder.pushTime
                block = {
                    vm.holder.resumeAnimation(it)
                    renderSync()
                }
            } + {
                time = vm.title.time
                block = {
                    vm.title.resumeAnimation(it)
                    renderSync()
                }
                ended = {start()}
            }
        }
    }
    override fun pause(base: ChHolderBase<View>, isJump:Boolean){
        if(isJump){
            Log.i("ch", "bbb")
            vm.paused()
            render()
        }else{
            Log.i("ch", "aaa")
            App.looper{
                time = Holder.popTime
                block = {
                    vm.holder.pauseAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.holder.paused()
                    renderSync()
                }
            }
        }
    }
    private fun start() {
        Log.i("ch", "start")
        vm.isLock = false
    }
}