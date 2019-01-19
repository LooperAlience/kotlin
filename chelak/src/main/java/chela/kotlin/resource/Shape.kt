package chela.kotlin.resource

import chela.kotlin.view.ChDrawable
import org.json.JSONObject

class Shape(val json:JSONObject){
    fun set(k:String) = ChDrawable.shape(k, json)
    fun remove(k:String) = ChDrawable.remove(k)
}