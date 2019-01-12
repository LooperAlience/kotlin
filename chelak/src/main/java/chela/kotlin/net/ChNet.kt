package chela.kotlin.net

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.core.*
import chela.kotlin.net.ChHttp.Companion.EXTRA_JSON
import chela.kotlin.net.ChHttp.Companion.EXTRA_REQUEST
import chela.kotlin.net.ChNet.apis
import chela.kotlin.regex.reParam
import chela.kotlin.sql.ChBaseDB.api
import chela.kotlin.sql.ChBaseDB.id
import chela.kotlin.validation.ChRuleSet
import okhttp3.Request
import org.json.JSONObject

typealias httpCallBack = (response: ChResponse)->Unit
typealias requestTaskF = (http:ChHttp, arg:MutableList<Pair<String, Any>>, taskArg:List<String>)->Boolean
typealias responseTaskF = (response:ChResponse, taskArg:List<String>)-> Boolean

/**
 * This object handles send HTTP request and read response.
 * It cached Api information on [apis].
 */
object ChNet {
    class Api(
        val url: String,
        val method: String,
        val requestTask: List<String>,
        val responseTask: List<String>,
        val request: Map<String, ApiRequest>
    )

    class ApiRequest(
        val name: String,
        val rules: String,
        val task: List<String>
    )

    @JvmStatic private val apis = mutableMapOf<String, Api>()
    @JvmStatic private var apiBaseURL = ""
    @JvmStatic private val requestItemTask = mutableMapOf<String, (Any) -> Any?>(
        "sha256" to {v->Ch.crypto.sha256("$v")}
    )
    @JvmStatic private val requestTask = mutableMapOf<String, requestTaskF>(
        "header" to {http, arg, taskArg->
            var cnt = 0
            arg.filter{taskArg.contains(it.first)}.forEach {
                http.header(it.first, "${it.second}")
                arg.remove(it)
                cnt++
            }
            cnt == taskArg.size
        },
        "json" to { http, arg, _->http.extra[EXTRA_JSON] = arg._stringify()
            true
        },
        "jsonBody" to { http, arg, _->
            http.json(http.extra[EXTRA_JSON]?.toString() ?: arg._stringify())
            true
        },
        "body" to { http, _, _->
            http.body(http.extra[EXTRA_REQUEST]?.toString() ?: http.extra[EXTRA_JSON]?.toString() ?: "")
            true
        }
    )
    @JvmStatic val timestamp = mutableMapOf<String, Long>()
    @JvmStatic private val responseTask = mutableMapOf<String, responseTaskF>(
        "json" to {res, _->
            res.body?.let{v->
                _try{JSONObject(v)}?.let{
                    res.extra[EXTRA_JSON] = it
                    res.result = it
                    true
                }
            } ?: false
        },
        "timestamp" to { res, arg->
            res.extra[EXTRA_JSON]?.let{
                (it as JSONObject)._long(arg[0])?.let{v->
                    val k = "${res.key}:${res.arg._toString()}"
                    timestamp[k]?.let{t -> if(v > t) timestamp[k] = v else false} ?:
                    run{timestamp[k] = v}
                }
            }
        true
        }
    )
    @JvmStatic fun apiBaseURL(url:String){
        apiBaseURL = url
        api.baseUrl(url)
    }
    @JvmStatic
    fun apiRequestTask(key: String, block: requestTaskF) {
        requestTask[key] = block
    }

    @JvmStatic
    fun apiRequestItemTask(key: String, block: (Any) -> Any?) {
        requestItemTask[key] = block
    }

    @JvmStatic
    fun apiResponseTask(key: String, block: responseTaskF) {
        responseTask[key] = block
    }

    @JvmStatic
    fun getApi(k: String): Api? {
        api.get()
        return apis[k]
    }
    @JvmStatic
    fun setApi(k: String, url: String, method: String, reqTask: String, resTask: String, req: Map<String, List<String>>, isWriteDB: Boolean = true){
        if (isWriteDB) api.addApi(k, url, method, reqTask, resTask)
        apis[k] = Api(url, method,
            if(reqTask.isNotBlank()) reqTask.split("|").map { it.trim() } else listOf(),
            if(resTask.isNotBlank()) resTask.split("|").map { it.trim() } else listOf(),
            with(mutableMapOf<String, ApiRequest>()) {
                req.forEach { (rk, v) ->
                    val (name, rule, task) = v
                    if (isWriteDB) api.addItem(rk, name, rule, task)
                    if (rule.isNotBlank() && rule.indexOf(".") == -1 && ChRuleSet["$k.$rk"] == null) ChRuleSet.set("$k.$rk", rule)
                    this[rk] = ApiRequest(name, rule, task.split("|").map { it.trim() })
                }
                this
            }
        )
    }
    /**
     * Parse json file list to MutableMap, and cached on [apis].
     * @param files json format file list.
     */
    @JvmStatic fun loadApi(files: List<String>) = files.forEach { v ->
        _try { JSONObject(v) }?.let { v ->
            if (id.isExist(v._string(id.ID) ?: "")) return@let
            v._string("base")?.let{apiBaseURL(it)}
            v._forObject { k, obj ->
                setApi(k,
                    obj._string(api.URL) ?: throw Exception("no url: $k"),
                    obj._string(api.METHOD) ?: "POST",
                    obj._string(api.REQUESTTASK) ?: "",
                    obj._string(api.RESPONSETASK) ?: "",
                    with(mutableMapOf<String, List<String>>()) {
                        obj._forObject(api.REQUEST){ rk, item ->
                            this[rk] = listOf(
                                item._string(api.REQUEST_NAME) ?: rk,
                                item._string(api.REQUEST_RULES) ?: "",
                                item._string(api.REQUEST_TASK) ?: ""
                            )
                        }
                        this
                    }
                )
            }
        }
    }


    /**
     * @key json object key on Api
     * @arg Pair you want to validate and send HTTP request.
     * <pre>
     *     Ch.net.api(jsonObjectKey, key to value...) { response ->
     *        App.data = response.result
     *     }
     * </pre>
     */
    @JvmStatic fun api(key:String, vararg arg:Pair<String, Any>, block:(ChResponse)->Unit):Ch.ApiResult{
        val api = getApi(key) ?: return Ch.ApiResult.fail("invalid api:$key")
        if(arg.size != api.request.size) return Ch.ApiResult.fail("invalid arg count:arg ${arg.size}, api ${api.request.size}")
        val reqItem = mutableListOf<Pair<String, Any>>()
        arg.forEach{(k, v)->
            val req = api.request[k] ?: return Ch.ApiResult.fail("invalid request param:$k")
            var r = v
            if(req.rules.isNotBlank()){
                r = Ch.ruleset.isOk(req.rules, r)
                if(r is ChRuleSet) return Ch.ApiResult.fail("rule check fail $k : $v")
            }
            req.task.forEach ch@{
                if(it.isBlank()) return@ch
                val task = requestItemTask[it] ?: return Ch.ApiResult.fail("invalid request item task:$it for $k")
                r = task(r) ?: return Ch.ApiResult.fail("request item task stop:$it for $k")
            }
            reqItem += (req.name) to r
        }
        if(reqItem.size != arg.size) return Ch.ApiResult.fail("invalid request param expected:${reqItem.size} actual:${arg.size}")
        val net = http(api.method, apiBaseURL + api.url)
        var msg = ""
        if(!api.requestTask.all {
            val (k, arg) = reParam.parse(it)
            return@all requestTask[k]?.let{
                if(!it(net, reqItem, arg)){
                    msg = "request task stop:$it for $key"
                    false
                }else true
            } ?: run{
                msg = "invalid request task:$it for $key"
                false
            }
        }) return Ch.ApiResult.fail(msg)
        net.send{
            var response = it
            response.key = key
            response.arg = reqItem
            api.responseTask.all {
                val (k, arg) = reParam.parse(it)
                responseTask[k]?.let {it(response, arg)} ?: run{
                    response.body = null
                    response.err = "invalid response task:$it for $key"
                    false
                }
            }
            block(response)
        }
        return Ch.ApiResult.ok
    }
    @JvmStatic fun http(method:String, url:String): ChHttp = ChHttpOk3(method, Request.Builder().url(url))
    @JvmStatic fun isOn():Boolean = connectedType() != Ch.NONE
    @JvmStatic fun connectedType():Ch.Value{
        if(SDK_INT < 23){
            ChApp.cm.activeNetworkInfo?.let{
                return when(it.type){
                    TYPE_WIFI -> Ch.WIFI
                    TYPE_MOBILE -> Ch.MOBILE
                    else -> Ch.NONE
                }
            }
        }else{
            ChApp.cm.activeNetwork?.let {
                val nc = ChApp.cm.getNetworkCapabilities(it)
                return when{
                    nc.hasTransport(TRANSPORT_WIFI) -> Ch.WIFI
                    nc.hasTransport(TRANSPORT_CELLULAR) -> Ch.MOBILE
                    else -> Ch.NONE
                }
            }
        }
        return Ch.NONE
    }
}