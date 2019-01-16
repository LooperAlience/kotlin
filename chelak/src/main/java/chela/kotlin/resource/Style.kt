package chela.kotlin.resource

import chela.kotlin.core._toJSON
import chela.kotlin.core._toMap
import chela.kotlin.core.toJSON
import chela.kotlin.view.ChStyle
import org.json.JSONObject

class Style(v:JSONObject):toJSON{
    private var map:Map<String, Any>? = v._toMap()
    fun set(k:String) = map?.let{ChStyle.add(k, it)}
    fun remove(k:String) = ChStyle.remove(k)
    override fun toJSON():String = map?._toJSON() ?: "{}"
}