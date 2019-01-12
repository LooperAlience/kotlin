package chela.kotlin.view

import android.util.Log
import chela.kotlin.core.*
import chela.kotlin.regex.reV
import chela.kotlin.sql.ChBaseDB
import chela.kotlin.sql.ChBaseDB.id
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
    @JvmStatic fun load(files:List<String>){
        files.forEach{v->
            _try{JSONObject(v)}?.let {v->
                if(id.isExist(v._string(id.ID) ?: "")) return@let
                v._forObject{k, obj->
                    val m = mutableMapOf<String, Any>()
                    var type = 0
                    if(k.startsWith("shape:")){
                        type = 1
                        ChDrawable.shape(k.substring(6), obj)
                    }else{
                        items[k] = m
                    }
                    ChBaseDB.style.addStyle(k)
                    obj._forValue{k, v->
                        val r = when(v){
                            is String -> {
                                (reV.match(v)?.let {
                                    it.groups[3]?.let { "d" + reV.group3(it) } ?: it.groups[4]?.let { "l" + reV.group4(it) }
                                } ?: "s$v")
                            }
                            is Int -> "i$v"
                            is Float -> "f$v"
                            is Long -> "l$v"
                            is Double -> "d$v"
                            is Boolean -> "b$v"
                            else -> "s$v"
                        }
                        if(type == 0) m[k] = r
                        ChBaseDB.style.addData(k, r)
                    }
                }
            }
        }
    }
    @JvmStatic operator fun get(k:String):Map<String, Any>?{
        ChBaseDB.style.get()
        return items[k]
    }
}