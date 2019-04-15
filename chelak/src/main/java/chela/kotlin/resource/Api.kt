package chela.kotlin.resource

import chela.kotlin.core._mapObject
import chela.kotlin.core._object
import chela.kotlin.core._string
import chela.kotlin.net.ChNet
import org.json.JSONObject


class Api(v:JSONObject, base:String){
    class ApiRequest(val name: String?, val rules: String?, val task: List<String>?)
    val url = v._string("url")?.let{"$base$it"} ?:""
    val method = v._string("method")?.let{it.toUpperCase()} ?: "POST"
    val requestTask = v._string("requesttask", "requestTask")?.split("|")?.map{it.trim()}
    val responseTask = v._string("responsetask", "responseTask")?.split("|")?.map{it.trim()}
    val request = v._object("request")?._mapObject{
        ApiRequest(
            it._string("name"),
            it._string("rules"),
            it._string("task")?.split("|")?.map{it.trim()}
        )
    }
    fun set(k:String) = ChNet.add(k, this)
    fun remove(k:String) = ChNet.remove(k)
}