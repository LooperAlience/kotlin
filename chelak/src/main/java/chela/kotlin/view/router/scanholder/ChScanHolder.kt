package chela.kotlin.view.router.scanholder

import android.util.Log
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.looper.ChLooper
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned
import chela.kotlin.view.scanner.ChScanner


abstract class ChScanHolder(private val layout:Int, private val model:ChScanHolderModel, private val tag:Any? = null): ChHolder<View>(){
    companion object{var looper:ChLooper? = null}

    private var scanned:ChScanned? = null
    protected fun render() = scanned?.render(null, model)
    override fun createInit(base:ChHolderBase<View>, vararg arg:Any){
        if(base !is ChGroupBase) throw Exception("")
        val view = base.inflate(layout)
        if(scanned == null) scanned = ChScanner.scan(Ch.NONE, view)
        scanInited()
        scanned!!.render(view, model)
    }
    override fun create(base:ChHolderBase<View>, vararg arg:Any) = scanned!!.view

    override fun addRestore(){
        model.wrapper.pushed()
        model.pushed()
        restored()
        scanned!!.render(null, model)
    }
    override fun addPop(){
    }
    override fun addPush(){
        if(looper != null && model.wrapper.push != Wrapper.Type.NO){
            looper?.invoke{
                time = Wrapper.pushTime.toInt()
                block = {
                    model.wrapper.pushAnimation(it)
                    model.pushAnimation(it)
                    scanned!!.renderSync(null, model)
                }
                ended = {
                    model.wrapper.pushed()
                    model.pushed()
                    pushed()
                    scanned!!.renderSync(null, model)

                }
            }
        }else{
            model.wrapper.pushed()
            model.pushed()
            pushed()
            scanned!!.render(null, model)
        }
    }
    override fun removePop() = if(looper != null && model.wrapper.pop != Wrapper.Type.NO){
        looper?.invoke{
            time = Wrapper.popTime.toInt()
            block = {
                model.wrapper.popAnimation(it)
                model.popAnimation(it)
                scanned!!.renderSync(null, model)
            }
            ended = {
                model.wrapper.poped()
                model.poped()
                poped()
                scanned!!.renderSync(null, model)
            }
        }
        Wrapper.popTime
    }else{
        model.wrapper.poped()
        model.poped()
        poped()
        scanned!!.render(null, model)
        0L
    }

    override fun removePush() {
    }
    override fun removeTake(){
        model.wrapper.poped()
        model.poped()
    }
    protected open fun scanInited(){}
    protected open fun restored(){}
    protected open fun pushed(){}
    protected open fun poped(){}
}