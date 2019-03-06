package chela.tutorial.holder

import android.util.Log
import android.view.View
import chela.kotlin.view.router.holder.ChHolderBase
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.SplashVM

object Splash:Scene(){
    @JvmStatic private val vm = SplashVM
    override fun layout() = R.layout.activity_splash
    override fun init(){}
    override fun push(base: ChHolderBase<View>, isRestore: Boolean){
        if(isRestore){
            vm.pushed()
            render()
            start()
        }else{
            App.looper{
                time = Holder.pushTime
                block = {
                    vm.holder.pushAnimation(it)
                    renderSync()
                }
            } + {
                time = vm.title.time
                block = {
                    vm.title.pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.pushed()
                    start()
                }
            }
        }
    }
    override fun pop(base: ChHolderBase<View>, isJump: Boolean) {
        if(isJump){
            Log.i("ch", "bbb")
            vm.poped()
            render()
        }else{
            Log.i("ch", "aaa")
            App.looper{
                time = Holder.popTime
                block = {
                    vm.holder.popAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.holder.poped()
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