package chela.tutorial.common

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned

abstract class Scene: ChHolder<View>(){
    private var inflater: LayoutInflater? = null
    protected var scan:ChScanned? = null

    override fun create(base: ChHolderBase<View>):View{
        if(base !is ChGroupBase) throw Exception("")
        vm()
        if(base.inflater != inflater) {
            inflater = base.inflater
            val view = base.inflate(layout())
            scan?.let{it.view = view} ?: run{scan = Ch.scanner.scan(this, view)}
        }
        scan!!.render()
        init()
        return scan!!.view
    }
    override fun push(base: ChHolderBase<View>, isRestore: Boolean) {
        Log.i("ch", "Scene push${isRestore}")
        if(isRestore){
            vm().pushed()
            render()
        }else{
            App.looper {
                Log.i("ch", "Scene looper")
                time = Holder.pushTime
                block = {
                    Log.i("ch", "Scene looper block")
                    vm().pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm().pushed()
                    pushed()
                }
            }
        }
    }
    override fun pop(base: ChHolderBase<View>, isJump: Boolean):Long {
        return if(isJump){
            Log.i("ch", "bbb")
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