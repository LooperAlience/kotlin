package chela.tutorial.holder

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
        if(base.inflater != inflater) {
            inflater = base.inflater
            val view = base.inflate(layout())
            scan?.let{it.view = view} ?: run{scan = Ch.scanner.scan(this, view)}
        }
        scan!!.render()
        init()
        return scan!!.view
    }
    fun render() = scan?.render()
    fun renderSync() = scan?.renderSync()
    abstract fun layout():Int
    abstract fun init()
}