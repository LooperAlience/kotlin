package chela.kotlin.net

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.core.toList
import chela.kotlin.regex.reParam
import chela.kotlin.validation.ChRuleSet
import okhttp3.Request
import org.json.JSONObject

typealias httpCallBack = (response: ChResponse)->Unit
typealias requestTaskF = (ChHttp, List<Pair<String, Any>>)->Boolean
typealias responseTaskF = (response:ChResponse)-> Boolean

/**
 * This object handles send HTTP request and read response.
 * It cached Api information on [apis].
 */
object ChNet{
    private class Api(
        val url:String,
        val method:String,
        val requestTask:List<String>,
        val request:Map<String, ApiRequest>,
        val responseTask:List<String>
    )
    private class ApiRequest(
        val name:String,
        val rules:String,
        val task:List<String>
    )

    @JvmStatic private val URL = "url"
    @JvmStatic private val METHOD = "method"
    @JvmStatic private val REQUESTTASK = "requestTsk"
    @JvmStatic private val REQUEST = "request"
    @JvmStatic private val REQUEST_NAME = "name"
    @JvmStatic private val REQUEST_RULES = "rules"
    @JvmStatic private val REQUEST_TASK = "task"
    @JvmStatic private val RESPONSETASK = "responseTask"

    @JvmStatic val ruleSet = mutableMapOf<String, ChRuleSet>()
    @JvmStatic private val apis = mutableMapOf<String, Api>()
    @JvmStatic private val requestTask = mutableMapOf<String, requestTaskF>()
    @JvmStatic private val requestItemTask = mutableMapOf<String, (Any)->Any?>()
    @JvmStatic private val responseTask = mutableMapOf<String, responseTaskF>()

    @JvmStatic fun apiRequestTask(key:String, block: requestTaskF){requestTask[key] = block}
    @JvmStatic fun apiRequestItemTask(key:String, block:(Any)->Any?){requestItemTask[key] = block}
    @JvmStatic fun apiResponseTask(key:String, block: responseTaskF){responseTask[key] = block}

    /**
     * Parse json file list to MutableMap, and cached on [apis].
     * @param files json format file list.
     */
    @JvmStatic fun loadApi(files:List<String>) = files.map{JSONObject(it)}.forEach{ v->
        v.keys().forEach{k->
            v.getJSONObject(k)?.let{json->
                apis[k] = Api(
                    json.getString(URL) ?: throw Exception("no url: $k"),
                    json.getString(METHOD)?.toUpperCase() ?: "POST",
                    json.getJSONArray(REQUESTTASK).toList(),
                    with(mutableMapOf<String, ApiRequest>()) {
                        json.getJSONObject(REQUEST)?.let { req ->
                            req.keys().forEach { rk ->
                                req.getJSONObject(rk)?.let { item ->
                                    val rule = item.getString(REQUEST_RULES)?.toUpperCase() ?: ""
                                    if(ruleSet["$k.$rk"] == null && !rule.contains(".")) ruleSet["$k.$rk"] = ChRuleSet(rule)
                                    this[rk] = ApiRequest(
                                        item.getString(REQUEST_NAME)?.toUpperCase() ?: rk,
                                        rule,
                                        item.getJSONArray(REQUEST_TASK).toList()
                                    )
                                }
                            }
                        }
                        this
                    },
                    json.getJSONArray(RESPONSETASK).toList()
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
        val api = apis[key] ?: return Ch.ApiResult.fail("invalid api:$key")
        if(arg.size != api.request.size) return Ch.ApiResult.fail("invalid arg count0")
        val reqItem = mutableListOf<Pair<String, Any>>()
        arg.forEach{(k, v)->
            val req = api.request[k] ?: return Ch.ApiResult.fail("invalid request param:$k")
            var r = v
            if(req.rules.isNotBlank()){
                r = Ch.rules.isOk(req.rules, r)
                if(r is ChRuleSet) return Ch.ApiResult.fail("rule check fail $k : $v")
            }
            req.task.forEach{
                val task = requestItemTask[it] ?: return Ch.ApiResult.fail("invalid request item task:$it for $k")
                r = task(r) ?: return Ch.ApiResult.fail("request item task stop:$it for $k")
            }
            reqItem += (req.name) to r
        }
        if(reqItem.size != arg.size) return Ch.ApiResult.fail("invalid request param expected:${reqItem.size} actual:${arg.size}")
        val net = http(api.method, api.url)
        var msg = ""
        if(!api.requestTask.all {
            return@all requestTask[it]?.let{
                if(!it(net, reqItem)){
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
                response.responseTaskParam.clear()
                response.responseTaskParam.addAll(arg)
                responseTask[k]?.let {it(response)} ?: run{
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