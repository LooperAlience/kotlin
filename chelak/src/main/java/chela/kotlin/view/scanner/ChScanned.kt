package chela.kotlin.view.scanner

import android.util.Log
import android.view.View
import chela.kotlin.model.Model
import chela.kotlin.thread.ChThread
import chela.kotlin.view.property.ChProperty

/**
 * This class stores the [items] scanned by the ChScanner.
 * The [collector] create ChScanItem field with the [items]'s key and value.
 */
class ChScanned internal constructor(@JvmField var view: View, private val items:MutableSet<ChScanItem> = mutableSetOf()):MutableSet<ChScanItem> by items{
    private val keyItem = mutableMapOf<String, ChScanItem>()
    /**
     * Restore the view.
     */
    fun render(v:View? = null, record: Model? = null):View{
        val collector = mutableSetOf<Pair<View, Map<String, Any>>>()
        val isNew = v != null && v !== view
        if(isNew) view = v!!
        items.forEach{
            if(isNew) it.view(view)
            val r = it.render(record)
            if(r.isNotEmpty()) collector += it.view to r
        }
        if(collector.isNotEmpty()) ChThread.msg(ChThread.property, collector)
        return view
    }
    fun renderSync(){
        items.forEach{
            val r = it.render()
            if(r.isNotEmpty()){
                val view = it.view
                r.forEach{(k, v)-> ChProperty.f(view, k.toLowerCase(), v) }
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