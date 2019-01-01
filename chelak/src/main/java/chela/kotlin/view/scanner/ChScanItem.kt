package chela.kotlin.view.scanner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import chela.kotlin.Ch
import chela.kotlin.core._shift
import chela.kotlin.view.ChStyle
import chela.kotlin.model.Model

class ChScanItem internal constructor(@JvmField var view: View, private val pos:List<Int>): Model(){
    @JvmField internal val collector = mutableMapOf<String, Any>()
    @JvmField internal var key = ""
    private var prop:MutableMap<String, List<String>>? = null
    private var propVal:MutableMap<String, Any>? = null
    private var record:MutableMap<String, List<String>>? = null
    private var recordVal:MutableMap<String, Any>? = null
    private var updater:MutableMap<String, Any>? = null
    private var once:MutableMap<String, Any>? = null
    private var isOnce = false
    internal fun view(v: View){
        var t = v
        for(i in pos) t = (t as ViewGroup).getChildAt(i)
        view = t
        propVal?.clear()
        recordVal?.clear()
        isOnce = false
    }
    private fun style(v:String) = ChStyle[v]?.let {
        it.forEach {(k, v) ->
            when {
                v is String && v[0] == '@' -> viewmodel(k, v.substring(2, v.length - 1).split("."))
                else -> set(k.toLowerCase(), v)
            }
        }
    }
    override operator fun set(k:String, v:Any):Boolean{
        if(v === OBJECT ||v === ARRAY) return true
        when {
            k.toLowerCase() == "style" -> style("$v")
            k[0] == '@' -> {
                if(updater == null) updater = mutableMapOf()
                updater?.put(k._shift(), v)
            }
            else -> {
                if(once == null) once = mutableMapOf()
                once?.put(k, v)
            }
        }
        return true
    }
    override fun viewmodel(k:String, v: List<String>):Boolean{
        if(k[0] == '-'){
            if(once == null) once = mutableMapOf()
            once?.put(k._shift(), Ch.model.get(v))
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
    fun render(recordViewModel: Model? = null):Boolean{
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
        prop?.let{
            it.forEach {(k, _v) ->
                val v = Ch.model.get(_v)
                if(k[0] == '@'){
                    collector[k._shift()] = v
                    isRender = true
                }else propVal?.let{
                    if(it[k] == null || it[k] != v) collector[k] = v
                    it[k] = v
                    isRender = true
                }
            }
        }
        record?.let{record->
            recordViewModel?.let{collector.putAll(record.mapValues{ (_, v)->
                Ch.model.record(v, it)
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