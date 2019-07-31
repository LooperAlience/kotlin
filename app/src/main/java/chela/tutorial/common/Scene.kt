package chela.tutorial.common

import android.view.View
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned
import chela.kotlin.view.scanner.ChScanner

abstract class Scene: ChHolder<View>(){
    protected var scan:ChScanned? = null
    override fun createInit(base:ChHolderBase<View>, vararg arg:Any){
        if(base !is ChGroupBase) throw Exception("")
        vm()
        val view = base.inflate(layout())
        if(scan == null) scan = ChScanner.scan(this, view)
        scan!!.render(view)
    }
    override fun create(base:ChHolderBase<View>, vararg arg:Any):View{
        init()
        return scan!!.view
    }
    override fun addRestore(){
        vm().pushed()
        render()
    }
    override fun addPush(){
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
    override fun removeTake(){
        vm().poped()
        render()
    }
    override fun removePop():Long{
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
        return Holder.popTime.toLong()
    }
    fun render() = scan?.render()
    fun renderSync() = scan?.renderSync()
    abstract fun vm(): SceneModel
    abstract fun layout():Int
    abstract fun init()
    abstract fun pushed()
}