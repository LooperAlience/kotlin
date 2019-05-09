package chela.kotlin.view.scanner

import android.view.View
import chela.kotlin.Ch
import chela.kotlin.model.Model
import chela.kotlin.thread.ChThread
import chela.kotlin.view.property.ChProperty

class ChScanned internal constructor(@JvmField var view: View, private val items:MutableSet<ChScanItem> = mutableSetOf()):MutableSet<ChScanItem> by items{
    private val keyItem = mutableMapOf<String, ChScanItem>()
    fun render(v:View? = null, record: Model? = null) =
        if(Ch.thread.isMain()) renderSync(v, record)
        else ChThread.main(Runnable {renderSync(v, record)})
    fun renderSync(v:View? = null, record: Model? = null){
        val isNew = v != null && v !== view
        if(isNew) view = v!!
        forEach{
            if(isNew) it.view(view)
            val r = it.render(record)
            if(r.isNotEmpty()){
                val view = it.view
                r.forEach{(k, v)-> ChProperty.f(view, k.toLowerCase(), v) }
            }
        }
    }
    fun subView(key:String):View? = keyItem[key]?.view
    override fun add(it: ChScanItem): Boolean {
        if(it.key.isNotBlank()) keyItem[it.key] = it
        return items.add(it)
    }
}