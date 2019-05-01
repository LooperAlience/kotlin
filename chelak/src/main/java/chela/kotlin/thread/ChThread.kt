package chela.kotlin.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import chela.kotlin.view.property.ChProperty
import chela.kotlin.view.scanner.ChScanItem
import java.util.concurrent.Executors
sealed class MsgType(val idx:Int){internal abstract fun f(it:Any)}
object Prop:MsgType(0){
    override fun f(it: Any){
        if(it !is Set<*>) return
        it.forEach {pair->
            if(pair !is Pair<*, *>) return
            (pair.first as? View)?.let{view->
                @Suppress("UNCHECKED_CAST")
                (pair.second as? Map<String, Any>)?.forEach{(k, v)->
                    ChProperty.f(view, k.toLowerCase(), v)
                }
            }
        }
    }
}
object ChThread{
    val property = Prop
    val msgType by lazy{mapOf<Int, MsgType>(0 to Prop)}
    private val mainHnd by lazy{object:Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg:Message){
            msgType[msg.what]?.f(msg.obj)
            msg.recycle()
        }
    }}
    private val que by lazy{Executors.newSingleThreadExecutor()}
    private val pool by lazy{Executors.newFixedThreadPool(3)}
    fun flushAll(){
        que.shutdownNow()
        pool.shutdownNow()
        mainHnd.looper.quit()
    }
    fun isMain(): Boolean = Looper.getMainLooper().thread === Thread.currentThread()
    fun que(task: Runnable) {que.execute(task)}
    fun pool(task: Runnable) {pool.execute(task)}
    fun main(task: Runnable) {main(0, task)}
    fun main(delay: Long, task: Runnable) {
        if (delay == 0L) {
            if (isMain()) task.run() else mainHnd.post(task)
        } else mainHnd.postDelayed(task, delay)
    }
    fun mainCancel(task: Runnable) {mainHnd.removeCallbacks(task)}
    fun msg(type: MsgType, v: Any) {msg(0, type, v)}
    fun msg(ms: Long, type:MsgType, v: Any) {
        val msg = mainHnd.obtainMessage(type.idx, v)
        if (ms == 0L) {
            if (isMain()) mainHnd.handleMessage(msg) else mainHnd.sendMessage(msg)
        } else mainHnd.sendMessageDelayed(msg, ms)
    }
    fun msgCancel(type: MsgType, v: Any) {mainHnd.removeMessages(type.idx, v)}
}