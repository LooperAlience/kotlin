package chela.tutorial.common

import android.view.LayoutInflater
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned
import chela.kotlin.view.scanner.ChScanner

abstract class Scene: ChHolder<View>(){
    protected var scan:ChScanned? = null
    private var id: Ch.Id? = null
    override fun create(base: ChHolderBase<View>, isRestore:Boolean, vararg arg:Any):View{
        if(base !is ChGroupBase) throw Exception("")
        vm()
        base.checkId(id)?.let{
            id = it
            val view = base.inflate(layout())
            scan?.let{it.view = view} ?: run{scan = ChScanner.scan(this, view)}
        }
        scan!!.render()
        init()
        return scan!!.view
    }
    override fun push(isRestore: Boolean) {
        if(isRestore){
            vm().pushed()
            render()
        }else{
            App.looper {
                time = Holder.pushTime
                block = {
                    vm().pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm().pushed()
                    pushed()
                    render()
                }
            }
        }
    }
    override fun pop(isJump: Boolean):Long {
        return if(isJump){
            vm().poped()
            render()
            0L
        }else{
            App.looper{
                time = Holder.popTime
                block = {
                    vm().popAnimation(it)
                    renderSync()
                }
                ended = {
                    vm().poped()
                    render()
                }
            }
            Holder.popTime.toLong()
        }
    }
    fun render() = scan?.render()
    fun renderSync() = scan?.renderSync()
    abstract fun vm(): SceneModel
    abstract fun layout():Int
    abstract fun init()
    abstract fun pushed()
}