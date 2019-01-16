package chela.kotlin.resource

import chela.kotlin.core.*
import chela.kotlin.i18n.ChI18n
import org.json.JSONObject

class I18n(v:JSONObject): toJSON {
    var isOne = ""
    val data = mutableMapOf<String, Map<String, String>>()
    init{
        v._string("isone", "isOne")?.let{isOne = it}
        v._object("data")?._forObject{k, obj->
            val m = mutableMapOf<String, String>()
            obj._forString{k, v->m[k] = v}
            data[k] = m
        }
    }
    fun set(k:String) = data?.let{ChI18n.add(k, this)}
    fun remove(k:String) = ChI18n.remove(k)

    override fun toJSON():String = """{"isone":"$isOne","data":${data._toJSON()}"""
}