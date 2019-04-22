package chela.kotlin.resource

import chela.kotlin.cdata.ChCdata
import chela.kotlin.core._for
import org.json.JSONObject

class Cdata(val cat:String):LinkedHashMap<String, Any?>(){
    var default:String? = null
    companion object{
        operator fun invoke(k:String, v:JSONObject, parent:Cdata = ChCdata.root){
            val i = k.indexOf('@')
            val data = (parent[k.substring(0, i)] as? Cdata) ?: run {
                val r = Cdata(k.substring(i))
                parent[k.substring(0, i)] = r
                r
            }
            v._for {k, v ->
                if(k == "@default" && v is String) data.default = v
                else if(k.contains('@')) (v as? JSONObject)?.let{invoke(k, it, data)}
                else data[k] = v
            }
        }
    }
}