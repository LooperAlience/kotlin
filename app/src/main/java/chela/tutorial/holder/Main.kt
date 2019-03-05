package chela.tutorial.holder

import android.view.View
import chela.kotlin.view.router.holder.ChHolderBase
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.MainVM


object Main : Scene() {
    @JvmStatic private val vm = MainVM
    override fun layout() = R.layout.main
    override fun resume(base: ChHolderBase<View>, isRestore: Boolean){
        if(isRestore){
            vm.resumed()
            render()
        }else{
            App.looper{
                time = Holder.pushTime
                block = {
                    vm.holder.resumeAnimation(it)
                   renderSync()
                }
            }
        }
    }
    override fun pause(base: ChHolderBase<View>, isJump:Boolean){
        if(isJump){
            vm.paused()
            render()
        }else{
            App.looper{
                time = Holder.popTime
                block = {
                    vm.holder.pauseAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.paused()
                    renderSync()
                }
            }
        }
    }
}