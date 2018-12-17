package chela.kotlin.viewmodel.scanner

import android.view.View
import android.view.ViewGroup
import chela.kotlin.thread.ChThread.MsgType.Prop
import chela.kotlin.thread.threadUtil
import chela.kotlin.core._shift
import chela.kotlin.viewmodel.ChViewModel
import chela.kotlin.viewmodel.properties
import chela.kotlin.viewmodel.recordFind
import chela.kotlin.viewmodel.vmFind

fun scan(id:Any, view:View): ChScanned = scanner.scan(id, view)
fun scanned(k:Any): ChScanned? = scanned[k]
private val scanner = ChScanner()
private val scanned = mutableMapOf<Any, ChScanned>()
class ChScanner {
    operator fun get(k:Any): ChScanned? = scanned[k]
    fun scan(id:Any, view:View): ChScanned {
        val prev = scanned[id]
        if(prev != null && prev.view == view) return prev
        val st = mutableListOf(view)
        val result = ChScanned(view)
        scanned[id] = result
        while(st.isNotEmpty()){
            val v = st.removeAt(st.size - 1)
            if(v.tag != null && v.tag is String){
                val pos = mutableListOf<Int>()
                var t = v
                while (t !== view) v.parent?.let {
                    val p = it as ViewGroup
                    pos += p.indexOfChild(v)
                    t = p
                }
                val target = ChScannedItem(v, pos)
                target.fromJson("{${v.tag}}")
                result += target
            }
            if (v is ViewGroup) for (i in v.childCount - 1 downTo 0) st.add(v.getChildAt(i))
        }
        return result
    }
}
class ChScanned internal constructor(var view:View, private val items:MutableSet<ChScannedItem> = mutableSetOf()):MutableSet<ChScannedItem> by items{
    private val collector = mutableSetOf<ChScannedItem>()
    fun render(v:View? = null):View{
        val isNew = v != null && v !== view
        if(isNew) view = v!!
        collector.clear()
        items.forEach{
            if(isNew) it.view(view)
            if(it.render()) collector += it
        }
        if(collector.isNotEmpty()) threadUtil.msg(Prop, collector)
        return view
    }
    fun renderSync(){
        items.forEach{
            if(it.render()){
                val view = it.view
                it.collector.forEach{(k, v)-> properties[k.toLowerCase()]?.f(view, v)}
            }
        }
    }
}
class ChScannedItem internal constructor(var view:View, private val pos:List<Int>): ChViewModel(){
    internal val collector = mutableMapOf<String, Any>()
    private var prop:MutableMap<String, List<String>>? = null
    private var propVal:MutableMap<String, Any>? = null
    private var record:MutableMap<String, List<String>>? = null
    private var recordVal:MutableMap<String, Any>? = null
    private var updater:MutableMap<String, Any>? = null
    private var once:MutableMap<String, Any>? = null
    private var isOnce = false
    internal fun view(v:View){
        var t = v
        for(i in pos) t = (t as ViewGroup).getChildAt(i)
        view = t
        propVal?.clear()
        recordVal?.clear()
        isOnce = false
    }
    override operator fun set(k:String, v:Any):Boolean{
        if(k[0] == '@'){
            if(updater == null) updater = mutableMapOf()
            updater?.put(k._shift(), v)
        }else{
            if(once == null) once = mutableMapOf()
            once?.put(k, v)
        }
        return true
    }
    override fun viewmodel(k:String, v: List<String>):Boolean{
        if(k[0] == '-'){
            if(once == null) once = mutableMapOf()
            once?.put(k._shift(), vmFind(v))
        }else{
            if(prop == null){
                prop = mutableMapOf()
                propVal = mutableMapOf()
            }
            prop?.put(k, v)
        }
        return true
    }
    override fun record(k:String, v: List<String>):Boolean{
        if(record == null){
            record = mutableMapOf()
            recordVal = mutableMapOf()
        }
        record?.put(k, v)
        return true
    }
    fun render(recordViewModel: ChViewModel? = null):Boolean{
        var isRender = false
        collector.clear()
        if(!isOnce){
            isOnce = true
            once?.let{
                isRender = true
                collector.putAll(it)
            }
        }
        updater?.let{
            isRender = true
            collector.putAll(it)
        }
        prop?.let{collector.putAll(it.mapValues{(_, v)-> vmFind(v) }.filter ch@{ (k, v)->
            propVal?.let{
                it[k]?.let{if(it == v) return@ch false}
                it.put(k, v)
                isRender = true
            }
            return@ch true
        })}
        record?.let{record->
            recordViewModel?.let{collector.putAll(record.mapValues{ (_, v)->
                recordFind(
                    v,
                    it
                )
            }.filter ch@{ (k, v)->
                recordVal?.let{
                    it[k]?.let{if(it == v) return@ch false}
                    it.put(k, v)
                    isRender = true
                }
                return@ch true
            }
        )}}
        return isRender
    }
}