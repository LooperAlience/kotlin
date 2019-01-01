package chela.kotlin.android

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.http.ChHttp
import chela.kotlin.http.ChHttpOk3
import okhttp3.Request
import org.json.JSONObject

private class Api(
    val url:String,
    val request:Map<String, String>,
    val response:Map<String, String>
)
object ChNet{
    @JvmStatic private val apis = mutableMapOf<String, Api>()
    @JvmStatic fun load(files:List<String>) = files.map{ JSONObject(it) }.forEach{v->
        v.keys().forEach{k->
            v.getJSONObject(k)?.let{data->
                val req = mutableMapOf<String, String>()
                val res = mutableMapOf<String, String>()
                data.getJSONObject("request")?.let{a->a.keys().forEach {k->req[k] = a.getString(k)}}
                data.getJSONObject("response")?.let{a->a.keys().forEach {k->res[k] = a.getString(k)}}
                apis[k] = Api(data.getString("url"), req, res)
            }
        }
    }
    @JvmStatic fun api(key:String, vararg arg:Pair<String, Any>):String = apis[key]?.let {api->
        if(arg.size != api.request.size) return "invalid arg count0"
        val json = mutableListOf<String>()
        arg.forEach{(k, v)->
            api.request[k]?.let{
                if(!Ch.rules.isOk(it to v)) return "rule check fail $k : $it"
                json.add("\"$k\":${if(v is String) "\"" + v.replace("\"", "\\\"") + "\"" else v}")
            } ?: run{return "invalid rule $k"}
        }
        http("POST", api.url).json("{${json.joinToString(",")}}").send{
            body, err, state ->
                Log.i("ch", body)
        }
        return ""
    } ?: ""
    @JvmStatic fun http(method:String, url:String): ChHttp = ChHttpOk3(method, Request.Builder().url(url))
    @JvmStatic fun isOn():Boolean = connectedType() != Ch.NONE
    @JvmStatic fun connectedType():Ch.Value{
        if(SDK_INT < 23){
            Ch.app.cm.activeNetworkInfo?.let{
                return when(it.type){
                    TYPE_WIFI -> Ch.WIFI
                    TYPE_MOBILE -> Ch.MOBILE
                    else -> Ch.NONE
                }
            }
        }else{
            Ch.app.cm.activeNetwork?.let {
                val nc = Ch.app.cm.getNetworkCapabilities(it)
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