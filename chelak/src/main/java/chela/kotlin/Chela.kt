package chela.kotlin

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.android.*
import chela.kotlin.core.ChDate
import chela.kotlin.core.ChMath
import chela.kotlin.core.ChReflect
import chela.kotlin.core._try
import chela.kotlin.crypto.ChCrypto
import chela.kotlin.cdata.ChCdata
import chela.kotlin.looper.ChLooper
import chela.kotlin.model.ChModel
import chela.kotlin.model.Model
import chela.kotlin.net.ChNet
import chela.kotlin.net.ChResponse
import chela.kotlin.resource.ChRes
import chela.kotlin.sql.ChSql
import chela.kotlin.thread.ChThread
import chela.kotlin.validation.ChRuleSet
import chela.kotlin.view.ChDrawable
import chela.kotlin.view.ChStyle
import chela.kotlin.view.ChView
import chela.kotlin.view.ChWindow
import chela.kotlin.view.property.ChProperty
import chela.kotlin.view.router.ChRouter
import chela.kotlin.view.router.holder.ChFragmentBase
import chela.kotlin.view.router.holder.ChGroupBase
import chela.kotlin.view.router.holder.ChHolderBase
import chela.kotlin.view.scanner.ChScanner
import net.sqlcipher.database.SQLiteDatabase
import org.json.JSONObject


/**
 * Chela base object
 */
object Ch{

    inline val Number.DptoPx get() = this.toDouble() * ChWindow.SptoPx
    inline val Number.PxtoDp get() = this.toDouble() * ChWindow.PxtoDp
    inline val Number.PxtoSp get() = this.toDouble() * ChWindow.PxtoSp
    inline val Number.SptoPx get() = this.toDouble() * ChWindow.SptoPx


    class Update(var v:Any)
    class Once(var v:Any){var isRun = false}
    class Id
    class ButtonDrawable(
        private val _top:Any?,
        private val _right:Any?,
        private val _bottom:Any?,
        private val _left:Any?
    ){
        companion object{
            fun d(v:Any?) = when(v){
                is String ->ChApp.drawable(v)
                is Drawable -> v
                else->null
            }
        }
        val top:Drawable? get() = d(_top)
        val right:Drawable? get() = d(_right)
        val bottom:Drawable? get() = d(_bottom)
        val left:Drawable? get() = d(_left)
    }

    enum class Value{obj, arr, wifi, mobile, none}

    val OBJECT = Value.obj
    val ARRAY = Value.arr
    val WIFI = Value.wifi
    val MOBILE = Value.mobile
    val NONE = Value.none

    val NONE_BA = ByteArray(0)
    private var isInited = false
    var debugLevel = 0
    fun isInited() = isInited

    operator fun invoke(application:Application, path:String = ""){
        if(isInited) throw Throwable("inited!")
        isInited = true
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        ChApp(application)
        SQLiteDatabase.loadLibs(application)
        ChRes.init()
        if (path.isNotBlank()) _try {
            JSONObject(ChAsset.string(path))
        }?.let {
            ChRes.load(it)
        }
        if (debugLevel == 0) Thread.currentThread().setUncaughtExceptionHandler { _, _ ->}
    }
    val math = ChMath
    val reflect = ChReflect
    val thread = ChThread
    val app = ChApp
    val window = ChWindow
    val res = ChRes
    val net = ChNet
    val content = ChContent
    val clipBoard = ChClipBoard
    val asset = ChAsset
    val shared = ChShared
    val keyboard = ChKeyboard
    val date = ChDate
    val permission = ChPermission
    val model = ChModel
    val sql = ChSql
    val query = Query
    val ruleset = ChRuleSet
    val view = ChView
    val drawable = ChDrawable
    val style = ChStyle
    val cdata = ChCdata
    val crypto = ChCrypto
    val scanner = ChScanner
    val prop = ChProperty

    object Query{
        fun chAdd(id:String, contents:String) = ChSql.db("ch").exec("ch_add", "id" to id, "contents" to contents)
        fun chId(id:String) = ChSql.db("ch").i("ch_id", "id" to id)
        fun chGet(id:String) = ChSql.db("ch").s("ch_getId", "id" to id)
    }
    @Suppress("SuspiciousEqualsCombination")
    fun isNone(v:Any):Boolean = v == NONE || v === NONE_BA
    fun value(_v:Any, data: Model? = null):Any{
        var v = _v
        while(v is String && v.isNotBlank()){
            v = when(v[0]){
                '@'->ChModel.get(v.substring(2, v.length - 1))
                '$'->if(data != null) ChModel.record(v.substring(2, v.length - 1), data)
                else throw Throwable("record but no data $v")
                else-> return v
            }
        }
        return v
    }
    fun waitActivate(activity:AppCompatActivity, looper:ChLooper? = null, f:()->Unit){
        (looper ?: run{
            val l = looper()
            l.act(activity)
            l
        }){
            isInfinity = true
            block = {
                if(activity.window.decorView.width != 0){
                    f()
                    it.stop()
                }
            }
        }
    }

    fun finish(act:AppCompatActivity){
        act.cacheDir?.let{it.deleteRecursively()}
        act.finish()
        System.exit(0)
    }

    fun looper():ChLooper = ChLooper()

    fun <T, R:ChHolderBase<T>>router(base: R): ChRouter<T, R> = ChRouter(base)
    fun groupBase():ChGroupBase = ChGroupBase()
    fun fragmentBase():ChFragmentBase = ChFragmentBase()

    sealed class ApiResult(val msg:String){
        fun isFail() = this is fail
        object ok:ApiResult("")
        class fail(msg:String):ApiResult(msg)
    }
    @Target(AnnotationTarget.PROPERTY) annotation class STRING(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class NUMBER(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class BOOLEAN(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class SHA256(val name:String = "")
    @Target(AnnotationTarget.PROPERTY) annotation class OUT(val name:String = "")
    /**
     * Interface for touch event(ex down, up, move)
     */
    interface Touch{fun onTouch(e: MotionEvent):Boolean}
    abstract class OnTextChanged:TextWatcher{
        lateinit var text: EditText
        override fun afterTextChanged(s:Editable?){}
        override fun beforeTextChanged(s:CharSequence?, start:Int, count:Int, after:Int){}
        override fun onTextChanged(s:CharSequence?, start:Int, before:Int, count:Int) = onChanged(text, s ?: "", start, before, count)
        abstract fun onChanged(view:EditText, s:CharSequence, start:Int, before:Int, count:Int)
        fun pos() = ChView.cursorPos(text)
    }
    abstract class Data <T>(val key:Any){
        companion object{
            private val data = mutableMapOf<Any, Any>()
            fun clear() = data.clear()
        }
        protected abstract fun getDB():T?
        protected abstract fun setDB(res:ChResponse?):Boolean
        protected abstract fun net(block:(ChResponse?)->Unit)
        protected abstract fun isValid(v:T):Boolean
        protected abstract fun data(v:T)
        protected open fun renew(v:T){}
        protected open fun error(){}
        operator fun invoke(retry:Int = 3){
            if(retry == 0){
                error()
                return
            }
            data[key]?.let{
                @Suppress("UNCHECKED_CAST")
                if(isValid(it as T)){
                    data(it)
                    return
                }else data.remove(key)
            }
            val v = getDB()
            if(v != null && isValid(v)){
                data[key] = v as Any
                renew(v)
                invoke(retry)
            }else net { res ->
                if(setDB(res)) invoke(retry - 1)
                else error()
            }
        }
    }
    fun click(block:(View)->Unit) = View.OnClickListener{block(it)}
}