package chela.kotlin.net

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.core.*
import chela.kotlin.regex.reParam
import chela.kotlin.sql.ChBaseDB.id
import chela.kotlin.sql.ChBaseDB.api
import chela.kotlin.validation.ChRuleSet
import okhttp3.Request
import org.json.JSONObject

typealias httpCallBack = (response: ChResponse)->Unit
typealias requestTaskF = (ChHttp, List<Pair<String, Any>>)->Boolean
typealias responseTaskF = (response:ChResponse)-> Boolean

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

    @JvmStatic
    private val apis = mutableMapOf<String, Api>()
    @JvmStatic
    private val requestTask = mutableMapOf<String, requestTaskF>()
    @JvmStatic
    private val requestItemTask = mutableMapOf<String, (Any) -> Any?>()
    @JvmStatic
    private val responseTask = mutableMapOf<String, responseTaskF>()

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
            reqTask.split("|").map { it.trim() },
            resTask.split("|").map { it.trim() },
            with(mutableMapOf<String, ApiRequest>()) {
                req.forEach { (rk, v) ->
                    val (name, rule, task) = v
                    if (isWriteDB) api.addItem(rk, name, rule, task)
                    if (rule.isNotBlank() && rule.indexOf(".") == -1) ChRuleSet.set("$k.$rk", rule)
                    this[rk] = ApiRequest(name, rule, task.split("|").map { it.trim() })
                }
                this
            }
        )
    }

    @JvmStatic
    fun loadApi(files: List<String>) = files.forEach { v ->
        _try { JSONObject(v) }?.let { v ->
            if (id.isExist(v._string(id.ID) ?: "")) return@let
            v._forObject { k, obj ->
                setApi(k,
                    obj._string(api.URL) ?: throw Exception("no url: $k"),
                    obj._string(api.METHOD) ?: "POST",
                    obj._string(api.RESPONSETASK) ?: "",
                    obj._string(api.RESPONSETASK) ?: "",
                    with(mutableMapOf<String, List<String>>()) {
                        obj._forObject { rk, item ->
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
    @JvmStatic fun api(key:String, vararg arg:Pair<String, Any>, block:(ChResponse)->Unit):Ch.ApiResult{
        val api = getApi(key) ?: return Ch.ApiResult.fail("invalid api:$key")
        if(arg.size != api.request.size) return Ch.ApiResult.fail("invalid arg count0")
        val reqItem = mutableListOf<Pair<String, Any>>()
        arg.forEach{(k, v)->
            val req = api.request[k] ?: return Ch.ApiResult.fail("invalid request param:$k")
            var r = v
            if(req.rules.isNotBlank()){
                r = Ch.ruleset.isOk(req.rules, r)
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