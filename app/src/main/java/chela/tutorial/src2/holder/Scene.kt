package chela.tutorial.src2.holder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned
import chela.tutorial.src2.App
import chela.tutorial.src2.viewmodel.Holder
import chela.tutorial.src2.viewmodel.SceneModel

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
        if(isRestore){
            Log.i("ch", "push1111111111")
            vm().pushed()
            render()
        }else{
            Log.i("ch", "push222222${Holder.pushTime}")
            vm().pushed()
            render()
            pushed()

            /*
            App.looper {

                time = Holder.pushTime
                block = {

                    Log.i("ch", "push33333")
                    vm().pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm().pushed()
                    pushed()
                }
            }
            */
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