package chela.kotlin.net

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.core._long
import chela.kotlin.core._stringify
import chela.kotlin.core._toString
import chela.kotlin.core._try
import chela.kotlin.net.ChHttp.Companion.EXTRA_JSON
import chela.kotlin.net.ChHttp.Companion.EXTRA_REQUEST
import chela.kotlin.net.ChNet.apis
import chela.kotlin.regex.reParam
import chela.kotlin.resource.Api
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
    @JvmStatic private val apis = mutableMapOf<String, Api>()
    @JvmStatic private val requestItemTask = mutableMapOf<String, (Any) -> Any?>(
        "sha256" to {v->Ch.crypto.sha256("$v")}
    )
    @JvmStatic private val requestTask = mutableMapOf<String, requestTaskF>(
        "header" to {http, arg, taskArg->
            var cnt = 0
            arg.filter{taskArg.contains(it.first)}.forEach {
                http.header(it.first, "${it.second}")
                cnt++
            }
            cnt == taskArg.size
        },
        "json" to { http, arg, _->http.extra[EXTRA_JSON] = arg._stringify()
            true
        },
        "jsonbody" to { http, arg, _->
            http.json(http.extra[EXTRA_JSON]?.toString() ?: arg._stringify())
            true
        },
        "body" to { http, arg, taskArg->
            http.body(
                if(taskArg.size == 1) "${arg.find{it.first == taskArg[0]}?.second ?: ""}"
                else http.extra[EXTRA_REQUEST]?.toString() ?: http.extra[EXTRA_JSON]?.toString() ?: ""
            )
            true
        }
    )
    @JvmStatic val timestamp = mutableMapOf<String, Long>()
    @JvmStatic private val responseTask = mutableMapOf<String, responseTaskF>(
        "json" to {res, _->
            (res.extra[EXTRA_JSON] ?: res.body)?.let{v->
                _try{JSONObject("$v")}?.let{
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
    @JvmStatic fun apiRequestTask(key: String, block: requestTaskF) {requestTask[key] = block}
    @JvmStatic fun apiRequestItemTask(key: String, block: (Any) -> Any?){ requestItemTask[key] = block}
    @JvmStatic fun apiResponseTask(key: String, block: responseTaskF){responseTask[key] = block}
    @JvmStatic fun get(k: String):Api? = apis[k]
    @JvmStatic fun add(k:String, api:Api){apis[k] = api}
    @JvmStatic fun remove(k:String) = apis.remove(k)
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
        val api = get(key) ?: return Ch.ApiResult.fail("invalid api:$key")
        val reqItem = mutableListOf<Pair<String, Any>>()
        api.request?.let{
            if(arg.size != it.size) return Ch.ApiResult.fail("invalid arg count:arg ${arg.size}, api ${it.size}")
            arg.forEach{(k, v)->
                val req = it[k] ?: return Ch.ApiResult.fail("invalid request param:$k")
                var r = v
                req.rules?.let {
                    if (it.isNotBlank()) {
                        r = Ch.ruleset.check(req.rules, r)
                        if (r is ChRuleSet) return Ch.ApiResult.fail("rule check fail $k:$v")
                    }
                }
                req.task?.forEach ch@{
                    if(it.isBlank()) return@ch
                    val task = requestItemTask[it] ?: return Ch.ApiResult.fail("invalid request item task:$it for $k")
                    r = task(r) ?: return Ch.ApiResult.fail("request item task stop:$it for $k")
                }
                reqItem += (req.name ?: k) to r
            }
        }
        val net = http(api.method, api.url)
        var msg = ""
        if(false == api.requestTask?.all{
            val (k, arg) = reParam.parse(it)
            return@all requestTask[k]?.let{
                if(!it(net, reqItem, arg)){
                    msg = "request task stop:$k for $key"
                    false
                }else true
            } ?: run{
                msg = "invalid request task:$k for $key"
                false
            }
        }) return Ch.ApiResult.fail(msg)
        if(Ch.debugLevel > 0){
            Log.i("ch", "method-" + api.method + ", url-" + api.url)
            if(Ch.debugLevel > 1) Log.i("ch", "requestArg-${arg.joinToString(", "){"${it.first}:${it.second}"}}")
            Log.i("ch", "requestItem-$reqItem")
        }
        net.send{res->
            res.key = key
            res.arg = reqItem
            api.responseTask?.all {
                val (k, arg) = reParam.parse(it)
                responseTask[k]?.let {it(res, arg)} ?: run{
                    res.err = "invalid response task:$it for $key"
                    false
                }
            }
            block(res)
        }
        return Ch.ApiResult.ok
    }
    @JvmStatic fun http(method:String, url:String): ChHttp = ChHttpOk3(method, Request.Builder().url(url))
    @JvmStatic fun isOn():Boolean = connectedType() != Ch.NONE

    @JvmStatic fun connectedType():Ch.Value{
        if(SDK_INT < 23){
            @Suppress("DEPRECATION")
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