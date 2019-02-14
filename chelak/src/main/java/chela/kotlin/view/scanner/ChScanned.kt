package chela.kotlin.view.scanner

import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.property.ChProperty

/**
 * This class stores the [items] scanned by the ChScanner.
 * The [collector] create ChScanItem field with the [items]'s key and value.
 */
class ChScanned internal constructor(@JvmField var view: View, private val items:MutableSet<ChScanItem> = mutableSetOf()):MutableSet<ChScanItem> by items{
    private val collector = mutableSetOf<ChScanItem>()
    private val keyItem = mutableMapOf<String, ChScanItem>()
    /**
     * Restore the view.
     */
    fun render(v: View? = null): View {
        val isNew = v != null && v !== view
        if(isNew) view = v!!
        collector.clear()
        items.forEach{
            if(isNew) it.view(view)
            if(it.render()) collector += it
        }
        if(collector.isNotEmpty()) Ch.thread.msg(Ch.thread.property, collector)
        return view
    }
    fun renderSync(){
        items.forEach{
            if(it.render()){
                val view = it.view
                it.collector.forEach{(k, v)-> ChProperty.f(view, k.toLowerCase(), v) }
            }
        }
    }
    fun subView(key:String):View? = keyItem[key]?.view

    /**
     * Plus assign [it] to ChScanned's item in the ChScanner object.
     */
    override fun add(it: ChScanItem): Boolean {
        if(it.key.isNotBlank()) keyItem[it.key] = it
        return items.add(it)
    }
}