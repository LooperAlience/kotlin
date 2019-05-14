package chela.kotlin.view.router.scanholder

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.kotlin.Ch.WrapperType.NO
import chela.kotlin.android.ChKeyboard
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanned
import chela.kotlin.view.scanner.ChScanner


abstract class ChScanHolder(private val layout:Int, private val model:ChScanHolderModel, private val tag:Any? = null): ChHolder<View>(){
    private var scanned:ChScanned? = null
    protected fun render() = scanned?.render(null, model)
    protected fun clearFocus() = scanned?.view?.clearFocus()
    protected fun keyboardHide() = (scanned?.view?.context as? AppCompatActivity)?.let{ ChKeyboard.hide(it)}
    protected fun openURLonBrower(url:String) = (scanned?.view?.context as? AppCompatActivity)?.startActivity(
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
    )
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
        if(model.wrapper.push != NO){
            ChGroupBase.looper{
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
    override fun removePop():Long {
        clearFocus()
        keyboardHide()
        return if (model.wrapper.pop != NO) {
            ChGroupBase.looper.invoke {
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
        } else {
            model.wrapper.poped()
            model.poped()
            poped()
            scanned!!.render(null, model)
            0L
        }
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