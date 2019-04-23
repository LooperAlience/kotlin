package chela.kotlin.view.scanner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import chela.kotlin.Ch
import chela.kotlin.core._shift
import chela.kotlin.model.ChModel
import chela.kotlin.model.Model
import chela.kotlin.view.ChStyle
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChViewModel
import com.chela.annotation.EX
import com.chela.annotation.PROP
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

/**
 * Scans the view component.
 * @param view has android tag value.
 * @param pos has the index of the child view.
 */
class ChScanItem internal constructor(@JvmField var view: View, private val pos:List<Int>): Model(){
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
    private fun style(it:Map<String,Any>){
        it.forEach {(k, v) ->
            when {
                v is String && v[0] == '@' -> viewmodel(k, v.substring(2, v.length - 1).split("."))
                else -> set(k.toLowerCase(), v)
            }
        }
    }
    /**
     * Store style attributes on [updater] or [once].
     * @param k If [k] is style or 0 index is @, then key of JSONObject or JSONArray.
     * Otherwise, [k] is key of style attribute. For example, "textcolor".
     * @param v String format with JSONObject, JSONArray or value of style attribute. For example, "#999999".
     */
    override operator fun set(k:String, v:Any):Boolean{
        if(v === OBJECT ||v === ARRAY) return true
        when {
            k.toLowerCase() == "style" ->"$v".split(",").map{it.trim()}.forEach{ChStyle[it]?.let{style(it)}}
            k[0] == '@' -> {
                if(updater == null) updater = mutableMapOf()
                updater?.put(k._shift(), v)
            }
            v is Ch.Update->{
                if(updater == null) updater = mutableMapOf()
                updater?.put(k._shift(), v.v)
            }
            else -> {
                if(once == null) once = mutableMapOf()
                once?.put(k, v)
            }
        }
        return true
    }

    /**
     * Get the style key and instance of the view component
     * @param k style key. For example, "style", "alpha", etc.
     * @param v Chela style identifier. For example, [SplashVM, holder], [SplashVM, holder, alpha], etc.
     * If k is a style, [v] join with style attributes by point separator.  For example, @{SplashVM.holder.alpha}, @{SplashVM.holder.visibility}, etc.
     * This process is repeated until the attribute is stored in [prop], [once], or [updater].
     */
    override fun viewmodel(k:String, v: List<String>):Boolean{
        if(k[0] == '-') {
            if (once == null) once = mutableMapOf()
            once?.put(k._shift(), ChModel.get(v))
        }else if(k == "style"){
            val m = mutableMapOf<String, Any>()
            val key = "@{" + v.joinToString(".")
            val target = ChModel.get(v)
            (target as? ChStyleModel)?.let{model->
                model::class.memberProperties.forEach { p->
                    if(p.findAnnotation<EX>() == null){
                        val name = p.name.toLowerCase()
                        m[name] = if(name == "style") target[p.name] else "$key.${p.name}}"
                    }
                }
            } ?: (target as? ChViewModel)?.let{model->
                model::class.memberProperties.forEach { p->
                    p.findAnnotation<PROP>()?.let{
                        val name = p.name.toLowerCase()
                        m[name] = if(name == "style") target[p.name] else "$key.${p.name}}"
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
        if(record == null){
            record = mutableMapOf()
            recordVal = mutableMapOf()
        }
        record?.put(k, v)
        return true
    }
    private fun value(v:Any) = when {
        v is String && v.isNotBlank() && v[0] == '@' -> ChModel.get(v.substring(2, v.length - 1))
        else -> v
    }
    fun render(recordViewModel:Model? = null):Map<String, Any>{
        val r = mutableMapOf<String, Any>()
        if(!isOnce){
            isOnce = true
            once?.let{r.putAll(it.mapValues{value(it.value)})}
        }
        updater?.let{r.putAll(it.mapValues{value(it.value)})}
        prop?.let{
            it.forEach {(k, _v) ->
                val v = value(ChModel.get(_v))
                propVal?.let{
                    if(it[k] == null || it[k] != v) r[k] = v
                    it[k] = v
                }
            }
        }
        record?.let{record->
            recordViewModel?.let{r.putAll(record.mapValues{ (_, v)->
                ChModel.record(v, it)
            }.filter ch@{ (k, v)->
                recordVal?.let{
                    it[k]?.let{if(it == v) return@ch false}
                    it.put(k, v)
                }
                return@ch true
            }
            )}
        }
        return r
    }
}