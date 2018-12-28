package chela.kotlin.android

import android.net.ConnectivityManager.TYPE_MOBILE
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build.VERSION.SDK_INT
import chela.kotlin.Ch
import chela.kotlin.http.ChHttp
import chela.kotlin.http.ChHttpOk3
import okhttp3.Request

object ChNet{
    @JvmStatic fun net(method:String, url:String): ChHttp = ChHttpOk3(method, Request.Builder().url(url))
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