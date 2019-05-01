package chela.kotlin.cdata

import chela.kotlin.Ch
import chela.kotlin.core._forObject
import chela.kotlin.core._try
import chela.kotlin.regex.reCdata
import chela.kotlin.resource.Cdata
import chela.kotlin.sql.ChSql
import org.json.JSONObject

object ChCdata{
    var cat = mutableMapOf<String, String>()
    var catDefault = mutableMapOf<String, String>()
    val cats:String get(){
        var r = ""
        cat.forEach {(k, v)->r += "&$k=$v"}
        return if(r.isEmpty()) r else r.substring(1)
    }
    private val memo = mutableMapOf<String, Any>()
    operator fun invoke(c:String, v:String){cat[c] = v}
    operator fun invoke(c:String) = cat[c] ?: ""
    operator fun invoke() = cats

    val root = Cdata("root")
    val req = mutableMapOf<String, MutableSet<String>>()
    private val rex = """\{|\}| """.toRegex()
    val requestKey:String? get() = if(Ch.cdata.req.isEmpty()) null else{
        val r ="${Ch.cdata.req}".replace(rex, "")
        ChCdata.req.clear()
        r
    }
    fun remove(k:String){root.remove(k)}
    fun save(k:String, v:String):String?{
        //정규식이 set에 여러개의 키가 있는 것을 아직 커버하지 않음
        if(!reCdata.isValidKey(k)) return "invalid key - $k"
        _try{JSONObject(v)}?.let{
            it._forObject{k, v->
                if(!reCdata.isValidJSONKey(k)) return "invalid json key - $k"
                Cdata(k, v)
            }
            Ch.query.chAdd(k, v)
            return null
        }
        return "invalid json - $v"
    }
    operator fun <T> get(k:String, request:MutableMap<String, MutableSet<String>> = req, retry:Int = 3):T?{
        if(retry == 0) return null
        val memoKey = "$k:$cats"
        memo[memoKey]?.let{
            @Suppress("UNCHECKED_CAST")
            return it as T
        }
        var target:Any? = root[k]
        if(target == null){
            val v = if(request[k] == null) ChSql.db("ch").s("ch_getCdata", "id" to "%$k=[*]%") else ""
            if(v.isBlank()) {
                if (request[k] == null) request[k] = mutableSetOf()
                request[k]?.add("*")
                return null
            }else _try{JSONObject(v)}?.let{
                it._forObject{k, v->Cdata(k, v)}
                return get(k, request, retry - 1)
            }
        }else if(request[k] != null){
            request[k]?.remove("*")
            if(request[k]?.isEmpty() == true) request.remove(k)
        }
        var i = 50
        var r = ""
        while(target != null && i-- > 0) (target as? Cdata)?.let {c->
            r += "${c.cat}=${cat[c.cat]}&"
            (c[cat[c.cat]] ?: c[c.default] ?: c[catDefault[if(c.default == null) c.cat else ""]])?.let{
                if(it is Cdata) target = it else{
                    if(k[0] != '@') memo[memoKey] = it
                    @Suppress("UNCHECKED_CAST")
                    return it as T
                }
            } ?: run {
                val id = r.substring(0, r.length - 1)
                val v = if(request[k] == null) ChSql.db("ch").s("ch_getCdata", "id" to "%$k=[$id]%") else ""
                if(v.isBlank()){
                    if(request[k] == null) request[k] = mutableSetOf()
                    request[k]?.add(id)
                    target = null
                }else _try{JSONObject(v)}?.let{
                    it._forObject{k, v->Cdata(k, v)}
                    return get(k, request, retry - 1)
                }
            }
        }
        return null
    }
}