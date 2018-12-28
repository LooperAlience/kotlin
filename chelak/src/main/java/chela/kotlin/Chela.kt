package chela.kotlin

import android.app.Application
import android.os.StrictMode
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.android.*
import chela.kotlin.core.ChDate
import chela.kotlin.core.ChReflect
import chela.kotlin.http.ChHttp
import chela.kotlin.http.ChHttpOk3
import chela.kotlin.looper.ChItem
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
     * Interface for touch event(ex down, up, move)
     */
    interface Touch{fun onTouch(e: MotionEvent):Boolean}
    interface Value
    /**
     * set base application
     */
    operator fun invoke(application:Application):Ch{
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        app(application)
        return this
    }
    @JvmStatic val WIFI = object:Value{}
    @JvmStatic val MOBILE = object:Value{}
    @JvmStatic val NONE = object:Value{}
    @JvmStatic val NONE_BA = ByteArray(0)
    @JvmStatic val reflect = ChReflect
    @JvmStatic val thread = ChThread
    @JvmStatic val app = ChApp
    @JvmStatic val window = ChWindow
    @JvmStatic val net = ChNet
    @JvmStatic val clipBoard = ChClipBoard
    @JvmStatic val asset = ChAsset
    @JvmStatic val shared = ChShared
    @JvmStatic val keyboard = ChKeyboard
    @JvmStatic val date = ChDate
    @JvmStatic val permission = ChPermission
    @JvmStatic val vm = viewmodel
    @JvmStatic val sql = ChSql

    @JvmStatic fun isNone(v:Any):Boolean = v === NONE || v === NONE_BA

    @JvmStatic fun waitActivate(activity:AppCompatActivity, looper:ChLooper? = null, block:()->Unit){
        with(if(looper == null){
            val l = Ch.looper()
            l.act(activity)
            l
        }else looper){
            invoke(infinity()){
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



    @JvmStatic fun looper():ChLooper = ChLooper()
    @JvmStatic fun time(ms:Int):ChLooper.Item.Time = ChLooper.Item.Time(ms)
    @JvmStatic fun delay(ms:Int):ChLooper.Item.Delay = ChLooper.Item.Delay(ms)

    @JvmStatic fun infinity():ChLooper.Item.Infinity = ChLooper.Item.Infinity()
    @JvmStatic fun ended(block:(ChItem)->Unit):ChLooper.Item.Ended = ChLooper.Item.Ended(block)
    /**
     * get router
     *
     */
    @JvmStatic fun <T>router(base: ChHolderBase<T>): ChRouter<T> = ChRouter(base)
    @JvmStatic fun groupBase():ChGroupBase = ChGroupBase()
    @JvmStatic fun fragmentBase():ChFragmentBase = ChFragmentBase()


    @JvmStatic val scanner = ChScanner
    @JvmStatic val prop = ChProperty
}