package chela.kotlin

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.android.*
import chela.kotlin.core.ChDate
import chela.kotlin.core.ChReflect
import chela.kotlin.http.ChHttp
import chela.kotlin.http.ChHttpOk3
import chela.kotlin.looper.ChLooper
import chela.kotlin.sql.ChSql
import chela.kotlin.thread.ChThread
import chela.kotlin.viewmodel.ChRouter
import chela.kotlin.viewmodel.holder.ChFragmentBase
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.viewmodel.holder.ChHolderBase
import chela.kotlin.viewmodel.property.ChProperty
import chela.kotlin.viewmodel.scanner.ChScanner
import chela.kotlin.viewmodel.viewmodel
import okhttp3.Request
/**
 * Chela base object
 */
object Ch{
    /**
     * set base application
     */
    operator fun invoke(application:Application):Ch{
        app.app(application)
        return this
    }
    @JvmStatic val NONE = object{}
    @JvmStatic val NONE_BA = ByteArray(0)
    @JvmStatic fun isNone(v:Any):Boolean = v === NONE || v === NONE_BA
    @JvmStatic fun waitActivate(activity:AppCompatActivity, looper:ChLooper? = null, block:()->Unit){
        with(if(looper == null){
            val l = Ch.looper()
            l.act(activity)
            l
        }else looper){
            add(ChLooper.Item.Time(1000)) {
                if(activity.window.decorView.width != 0){
                    block()
                    it.stop()
                }
            }
        }
    }
    @JvmStatic fun finish(act:AppCompatActivity){
        act.finish()
        System.exit(0)
    }

    @JvmStatic fun net(method:String, url:String):ChHttp = ChHttpOk3(method, Request.Builder().url(url))
    @JvmStatic val reflect = ChReflect
    @JvmStatic val thread = ChThread
    @JvmStatic val app = ChApp
    @JvmStatic val asset = ChAsset
    @JvmStatic val shared = ChShared
    @JvmStatic val keyboard = ChKeyboard
    @JvmStatic val date = ChDate
    @JvmStatic val permission = ChPermission
    @JvmStatic val vm = viewmodel
    @JvmStatic val sql = ChSql

    /**
     * get router
     *
     */
    @JvmStatic fun <T>router(base: ChHolderBase<T>): ChRouter<T> = ChRouter(base)
    @JvmStatic fun groupBase():ChGroupBase = ChGroupBase()
    @JvmStatic fun fragmentBase():ChFragmentBase = ChFragmentBase()
    @JvmStatic fun looper():ChLooper = ChLooper()
    @JvmStatic val scanner = ChScanner
    @JvmStatic val prop = ChProperty
}