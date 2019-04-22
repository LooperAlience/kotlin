package chela.kotlin.cdata

import chela.kotlin.resource.Cdata
import java.net.URLEncoder

object ChCdata{
    var cat = mutableMapOf<String, String>()
    var catDefault = mutableMapOf<String, String>()
    val cats:String get(){
        val r = mutableListOf<String>()
        cat.forEach {(k, v)->r.add("${URLEncoder.encode(k, "UTF-8")}=${URLEncoder.encode(v, "UTF-8")}")}
        return r.joinToString("&")
    }
    operator fun invoke(c:String, v:String){cat[c] = v}
    operator fun invoke(c:String) = cat[c] ?: ""
    operator fun invoke() = cats

    val root = Cdata("root")
    val req = mutableMapOf<String, String>()
    fun remove(k:String){root.remove(k)}

    operator fun <T> get(k:String, request:MutableMap<String, String> = req):T?{
        var target:Any? = root[k]
        var r = "$k["
        if(target == null) {
            request[k] = "$r]"
            return null
        }
        var i = 100
        while(target != null && i-- > 0){
            (target as? Cdata)?.let {
                r += "${it.cat}=${cat[it.cat]}&"
                (it[cat[it.cat]] ?: it[it.default] ?: it[catDefault[if(it.default == null) it.cat else ""]])?.let{
                    @Suppress("UNCHECKED_CAST")
                    if(it is Cdata) target = it else return it as T
                } ?: run {
                    request[k] = r.substring(0, r.length - 1) + "]"
                    target = null
                }
            }
        }
        return null
    }
}