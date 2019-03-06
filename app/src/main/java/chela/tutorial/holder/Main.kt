package chela.tutorial.holder

import android.view.View
import chela.kotlin.view.router.holder.ChHolderBase
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.MainVM


object Main : Scene() {
    @JvmStatic private val vm = MainVM
    override fun layout() = R.layout.activity_main
    override fun init(){}
    override fun push(base: ChHolderBase<View>, isRestore: Boolean){
        if(isRestore){
            vm.pushed()
            render()
        }else{
            App.looper{
                time = Holder.pushTime
                block = {
                    vm.holder.pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.pushed()
                    vm.isLock = false
                }
            }
        }
    }
    override fun pop(base: ChHolderBase<View>, isJump:Boolean){
        if(isJump){
            vm.poped()
            render()
        }else{
            App.looper{
                time = Holder.popTime
                block = {
                    vm.holder.popAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.poped()
                    renderSync()
                }
            }
        }
    }
}