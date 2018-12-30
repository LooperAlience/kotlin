package chela.kotlin

import android.app.Application
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.android.*
import chela.kotlin.core.*
import chela.kotlin.looper.ChItem
import chela.kotlin.looper.ChLooper
import chela.kotlin.sql.ChSql
import chela.kotlin.thread.ChThread
import chela.kotlin.validation.ChRuleSet
import chela.kotlin.viewmodel.ChRouter
import chela.kotlin.viewmodel.holder.ChFragmentBase
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.viewmodel.holder.ChHolderBase
import chela.kotlin.viewmodel.property.ChProperty
import chela.kotlin.viewmodel.scanner.ChScanner
import chela.kotlin.viewmodel.viewmodel
import java.io.File

/**
 * Chela base object
 */
object Ch{
    @Target(AnnotationTarget.PROPERTY) annotation class STRING(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class NUMBER(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class BOOLEAN(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class SHA256(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class OUT(val name:String = "")
    /**
     * Interface for touch event(ex down, up, move)
     */
    interface Touch{fun onTouch(e: MotionEvent):Boolean}
    interface Value
    abstract class OnTextChanged:TextWatcher{
        lateinit var text: EditText
        override fun afterTextChanged(s:Editable?){}
        override fun beforeTextChanged(s:CharSequence?, start:Int, count:Int, after:Int){}
        override fun onTextChanged(s:CharSequence?, start:Int, before:Int, count:Int) = onChanged(text, s ?: "", start, before, count)
        abstract fun onChanged(view:EditText, s:CharSequence, start:Int, before:Int, count:Int)
        fun pos() = ChView.cursorPos(text)
    }
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
    @JvmStatic val math = ChMath
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
    @JvmStatic val rules = ChRules
    @JvmStatic val view = ChView
    @JvmStatic val crypto = ChCrypto
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
        act.cacheDir?.let{it.deleteRecursively()}
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