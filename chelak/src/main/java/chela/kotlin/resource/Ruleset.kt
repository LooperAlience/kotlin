package chela.kotlin.resource

import chela.kotlin.core.*
import chela.kotlin.validation.ChRuleSet
import org.json.JSONObject

class Ruleset(v:JSONObject):toJSON{
    private var map:Map<String, String>? = v._mapValue()
    fun set(k:String) = map?.forEach{(key, v)->ChRuleSet.add("$k.$key", v)}
    fun remove(k:String) = map?.forEach{(key, _)->ChRuleSet.remove("$k.$key")}
    override fun toJSON():String = map?._toJSON() ?: "{}"
}