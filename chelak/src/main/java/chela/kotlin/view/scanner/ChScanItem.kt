package chela.kotlin.view.scanner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import chela.kotlin.Ch
import chela.kotlin.model.ChModel
import chela.kotlin.model.Model
import chela.kotlin.regex.reV
import chela.kotlin.view.ChStyle
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChViewModel
import com.chela.annotation.EX
import com.chela.annotation.PROP
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class ChScanItem internal constructor(@JvmField var view: View, private val pos:List<Int>): Model(){
    @JvmField internal var key = ""
    private var prop:MutableMap<String, List<String>>? = null
    private var propVal:MutableMap<String, Any>? = null
    private var record:MutableMap<String, List<String>>? = null
    private var recordVal:MutableMap<String, Any>? = null
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
    private fun style(it:Map<String,Any>){
        it.forEach{(k, v) ->
            if(v is String && v[0] == '@') viewmodel(k, v.substring(2, v.length - 1).split("."))
            else set(k.toLowerCase(), v)
        }
    }
    override operator fun set(k:String, v:Any):Boolean{
        if(v == Ch.OBJECT ||v == Ch.ARRAY) return true
        when (k) {
            "style" -> "$v".split(",").map{it.trim()}.forEach{ChStyle[it]?.let{style(it)}}
            else -> {
                if(once == null) once = mutableMapOf()
                once?.put(k, v)
            }
        }
        return true
    }
    override fun viewmodel(k:String, v: List<String>):Boolean{
        if(k == "style"){
            val m = mutableMapOf<String, Any>()
            val key = "@{" + v.joinToString(".")
            when(val target = ChModel.get(v)){
                is ChStyleModel->{
                    val anno = target.ref.annotation
                    target.ref.getter.forEach{(k, _)->
                        if(anno[k] != "EX") m[k] = if(k == "style") target[k] else "$key.$k}"
                    }
                }
                is ChViewModel->{
                    val anno = target.ref.annotation
                    target.ref.getter.forEach{(k, _)->
                        if(anno[k] == "PROP") m[k] = if(k == "style") target[k] else "$key.$k}"
                    }
                }
            }
            if(m.isNotEmpty()) style(m)
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
        if (record == null) {
            record = mutableMapOf()
            recordVal = mutableMapOf()
        }
        record?.put(k, v)
        return true
    }

    fun render(data:Model? = null):Map<String, Any>{
        val r = mutableMapOf<String, Any>()
        if(!isOnce){
            isOnce = true
            once?.let{
                it.forEach{(k, v)->r[k] = v}
                once = null
            }
        }
        prop?.forEach{(k, _v) ->
            when(val v = Ch.value(ChModel.get(_v), data)){
                is Ch.Once->{
                    r[k] = Ch.value(v.v)
                    v.isRun = true
                    prop?.remove(k)
                }
                is Ch.Update->r[k] = Ch.value(v.v)
                else->propVal?.let{
                    val pv = it[k]
                    if(pv == null || pv != v){
                        r[k] = v
                        it[k] = v
                    }
                }
            }
        }
        record?.forEach{(k, _v)->
            if(data == null) throw Throwable("no data for record")
            val v = Ch.value(ChModel.record(_v, data))
            when{
                k == "style" -> (v as? Model)?.ref?.getter?.forEach {(k, f) ->
                    when(val sv = Ch.value(f.call(v) ?: Ch.NONE)){
                        is Ch.Once -> if (!sv.isRun) {
                            r[k] = Ch.value(sv.v)
                            sv.isRun = true
                        }
                        is Ch.Update -> r[k] = Ch.value(sv.v)
                        else -> recordVal?.let {
                            val pv = it["style.$k"]
                            if (pv == null || pv != sv) {
                                r[k] = sv
                                it["style.$k"] = sv
                            }
                        }
                    }
                }
                v is Ch.Once -> {
                    r[k] = Ch.value(v.v)
                    v.isRun = true
                    record?.remove(k)
                }
                v is Ch.Update -> r[k] = Ch.value(v.v)
                else -> {
                    recordVal?.let {
                        if (it[k] == null || it[k] != v){
                            r[k] = v
                            it[k] = v
                        }
                    }
                }
            }
        }
        if(r.isNotEmpty()) r.forEach{(k, v)->
            r[k] = if(v is String && v.indexOf(' ') == -1) reV.num(v)?.toFloat() ?: v
            else v
        }
        return r
    }
}