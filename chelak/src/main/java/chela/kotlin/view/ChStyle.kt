package chela.kotlin.view

import chela.kotlin.core.*
import chela.kotlin.core.ChSchema.Setting
import chela.kotlin.regex.reV
import chela.kotlin.sql.ChBaseDB
import org.json.JSONObject

/**
 * This object cached style property on [items].
 */
object ChStyle{
    @JvmStatic val items = mutableMapOf<String, Map<String, Any>>()
    /**
     * Parse json file list to MutableMap.
     * @files json format file list
     * <pre> For example, style.json
     * {
     *   "footer":{
     *     "textColor":"#ffffff",
     *     "textSize":"10sp",
     *     "text":["footer/footer", 1, {}]
     *    }
     *   ...
     * }
     * </pre>
     */
    @JvmStatic fun load(files:List<String>) = files.forEach{v->
        _try{JSONObject(v)}?.let {v->
            if(ChBaseDB.id.isExist(v._string(Setting.ID) ?: "")) return@let
            v._forObject{k, obj->
                if (items[k] != null) throw Exception("exist style:$k")
                val m = mutableMapOf<String, Any>()
                items[k] = m
                ChBaseDB.style.addStyle(k)
                obj._forValue{k, v->
                    val r = when(v){
                        is String -> {
                            "s" + (reV.match(v)?.let {
                                it.groups[3]?.let { reV.group3(it) } ?: it.groups[4]?.let { reV.group4(it) }
                            } ?: v)
                        }
                        is Int -> "i$v"
                        is Float -> "f$v"
                        is Long -> "l$v"
                        is Double -> "d$v"
                        is Boolean -> "b$v"
                        else -> "s$v"
                    }
                    m[k] = r
                    ChBaseDB.style.addData(k, r)
                }
            }
        }
    }
    @JvmStatic operator fun get(k:String):Map<String, Any>?{
        ChBaseDB.style.get()
        return items[k]
    }
}