package chela.kotlin

import android.app.Application
import chela.kotlin.android.ChApp
import chela.kotlin.android.ChAsset
import chela.kotlin.android.ChShared
import chela.kotlin.core.ChReflect
import chela.kotlin.http.ChHttp
import chela.kotlin.http.ChHttpOk3
import chela.kotlin.looper.ChLooper
import chela.kotlin.sql.ChSql
import chela.kotlin.thread.ChThread
import chela.kotlin.validation.ChTypeValidator
import chela.kotlin.viewmodel.ChProperty
import chela.kotlin.viewmodel.ChRouter
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.viewmodel.holder.ChHolderBase
import chela.kotlin.viewmodel.scanner.ChScanner
import chela.kotlin.viewmodel.viewmodel
import okhttp3.Request

object Ch{
    operator fun invoke(application:Application):Ch{
        app.app(application)
        return this
    }
    @JvmStatic val NONE = object{}
    @JvmStatic val NONE_BA = ByteArray(0)
    @JvmStatic fun isNone(v:Any):Boolean = v === NONE || v === NONE_BA
    @JvmStatic fun net(method:String, url:String):ChHttp = ChHttpOk3(method, Request.Builder().url(url))
    @JvmStatic val reflect = ChReflect
    @JvmStatic val vm = viewmodel
    @JvmStatic val thread = ChThread
    @JvmStatic val app = ChApp
    @JvmStatic val asset = ChAsset
    @JvmStatic val shared = ChShared
    @JvmStatic val sql = ChSql
    @JvmStatic fun <T>router(base: ChHolderBase<T>): ChRouter<T> = ChRouter(base)
    @JvmStatic fun groupBase():ChGroupBase = ChGroupBase()
    @JvmStatic fun looper():ChLooper = ChLooper()
    @JvmStatic val scanner = ChScanner
    @JvmStatic val prop = ChProperty

    @JvmStatic val typeValidator = ChTypeValidator
}
