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


abstract class ChScanHolder(private val layout:Int, private val model: ChScanHolderModel, name:String = ""): ChHolder<View>(name){
    companion object{var looper:ChLooper? = null}
    private var id: Ch.Id? = null
    private var scanned:ChScanned? = null
    protected fun render() = scanned?.render(null, model)
    override fun create(base:ChHolderBase<View>, isRestore:Boolean, vararg arg:Any):View{
        if(base !is ChGroupBase) throw Exception("")
        var view:View? = null
        base.checkId(id)?.let{
            id = it
            view = base.inflate(layout)
            if(scanned == null) scanned = ChScanner.scan(Ch.NONE, view!!)
        }
        scanInited()
        scanned!!.render(view, model)
        return scanned!!.view
    }
    override fun push(isRestore:Boolean){
        if(isRestore || looper == null || model.wrapper.push === Wrapper.Type.NO){
            restored()
            model.wrapper.pushed()
            model.pushed()
            scanned!!.render(null, model)
        }else{
            created()
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
                    pushed(isRestore)
                    scanned!!.renderSync(null, model)
                }
            }
        }
    }
    override fun pop(isRestore:Boolean) =
        if(isRestore || looper == null || model.wrapper.pop === Wrapper.Type.NO){
            model.wrapper.poped()
            model.poped()
            poped(isRestore)
            scanned!!.render(null, model)
            0L
        }else{
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
                    poped(isRestore)
                    scanned!!.renderSync(null, model)
                }
            }
            Wrapper.popTime
        }
    protected open fun scanInited(){}
    protected open fun restored(){}
    protected open fun created(){}
    protected open fun pushed(isRestore:Boolean){}
    protected open fun poped(isRestore:Boolean){}
}