package chela.kotlin.resource

import chela.kotlin.core._mapValue
import chela.kotlin.regex.reV
import chela.kotlin.view.ChStyle
import org.json.JSONObject

class Style(v:JSONObject){
    private var map:Map<String, Any>? = v._mapValue()
    fun set(k:String) = map?.let{
        ChStyle.add(k, it.mapValues{(_, v)->
            if(v is String && v.indexOf(' ') == -1) reV.num(v)?.toFloat() ?: v
            else v
        })
    }
    fun remove(k:String) = ChStyle.remove(k)
}