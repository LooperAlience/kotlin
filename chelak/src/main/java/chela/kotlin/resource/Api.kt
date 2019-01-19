package chela.kotlin.resource

import chela.kotlin.core._forObject
import chela.kotlin.core._object
import chela.kotlin.core._string
import chela.kotlin.net.ChNet
import org.json.JSONObject

class ApiRequest(
    val name: String?,
    val rules: String?,
    val task: List<String>?
)
class Api(v:JSONObject){
    var url: String = ""
    var method: String = "POST"
    var requestTask: List<String>? = null
    var responseTask: List<String>? = null
    var request: Map<String, ApiRequest>? = null
    init{
        v._string("url")?.let{url = it}
        v._string("method")?.let{method = it.toUpperCase()}
        v._string("requesttask", "requestTask")?.let{
            if(it.isNotBlank()) requestTask = it.split("|").map{it.trim()}
        }
        v._string("responsetask", "responseTask")?.let{
            if(it.isNotBlank()) responseTask = it.split("|").map{it.trim()}
        }
        v._object("request")?.let{
            val r = mutableMapOf<String, ApiRequest>()
            it._forObject{k, v->
                r[k] = ApiRequest(
                    v._string("name"),
                    v._string("rules"),
                    v._string("task")?.let { it.split("|").map { it.trim() } } ?: null
                )
            }
        }
    }
    fun set(k:String) = ChNet.add(k, this)
    fun remove(k:String) = ChNet.remove(k)
}