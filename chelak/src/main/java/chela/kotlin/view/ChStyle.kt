package chela.kotlin.view

import chela.kotlin.regex.reV
import org.json.JSONObject

object ChStyle {
    @JvmStatic private val items = mutableMapOf<String, Map<String, Any>>()
    @JvmStatic fun load(files:List<String>) = files.map{JSONObject(it)}.forEach{v->
        v.keys().forEach{k->
            if(items[k] != null) throw Exception("exist key:$k")
            val m = mutableMapOf<String, Any>()
            val t = v.getJSONObject(k)
            items[k] = m
            t.keys().forEach{
                val tv = t.get(it)
                m[it] = when(tv){
                    is String -> {
                        val s = t.getString(it)
                        reV.match(s)?.let {
                            it.groups[3]?.let {reV.group3(it)} ?: it.groups[4]?.let {reV.group4(it)}
                        } ?: s
                    }
                    else -> tv
                }
            }
        }
    }
    @JvmStatic operator fun get(k:String):Map<String, Any>? = items[k]
}