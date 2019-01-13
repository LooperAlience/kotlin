package chela.kotlin.i18n

import chela.kotlin.core.*
import chela.kotlin.sql.ChBaseDB
import chela.kotlin.sql.ChBaseDB.i18n
import org.json.JSONObject
import java.lang.Exception

object ChI18n{
    class I18n(
        val ver:Int,
        val isOne:String,
        val data:Map<String, Map<String, String>>
    )
    @JvmStatic private var lang = ""
    @JvmStatic private val items = mutableMapOf<String, I18n>()
    @JvmStatic fun set(k:String, ver:Int, isOne:String, data:Map<String, Map<String, String>>, isWriteDB:Boolean = true){
        i18n.get()
        if(isWriteDB){
            i18n.addKey(k, ver, isOne)
            data.forEach{(k, v)->
                v.forEach{(rk, v)->i18n.addData(k, rk, v)}
            }
        }
        items[k] = I18n(ver, isOne, data)
    }
    @JvmStatic fun getItem(k:String):I18n?{
        i18n.get()
        return items[k]
    }
    @JvmStatic operator fun get(k:String):String = get("i18n.$k".split(".").map {it.trim()})
    @JvmStatic operator fun get(k:List<String>):String{
        if(k.size < 3) throw Exception("invalid key:$k")
        val (_, key, subKey) = k
        val i = getItem(key) ?: return "no data:$key"
        val ln = if(k.size == 4) k[3] else ""
        return i.data[
                if(i.isOne.isNotBlank()) i.isOne
                else if(ln != "") ln
                else lang
            ]?.get(subKey) ?: "no data:$k"
    }
    @JvmStatic operator fun invoke(ln:String){lang = ln}
    @JvmStatic operator fun invoke() = lang
    @JvmStatic fun load(files:List<String>) = files.forEach{v->
        _try{JSONObject(v)}?.let ch@{v->
            if(ChBaseDB.id.isExist(v._string(ChBaseDB.id.ID) ?: "")) return@ch
            v._forObject { k, obj ->
                set(k,
                    obj._int(i18n.VER) ?: 1,
                    obj._string(i18n.ISONE) ?: "",
                    with(mutableMapOf<String,Map<String, String>>()){
                        obj._forObject(i18n.DATA){ k, obj->
                            val m = mutableMapOf<String, String>()
                            this[k] = m
                            obj._forString { k, v ->m[k] = v}
                        }
                        this
                    })
                }
            }
        }
}