package chela.kotlin.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import chela.kotlin.view.property.ChProperty
import chela.kotlin.view.scanner.ChScanItem
import java.util.concurrent.Executors
sealed class MsgType(val idx:Int){internal abstract fun f(it:Any)}
object Prop:MsgType(0){override fun f(it: Any){
    if(it !is Set<*>) return
    it.forEach {
        if(it !is ChScanItem) return
        val view = it.view
        it.collector.forEach{(k, v)-> ChProperty.f(view, k.toLowerCase(), v)}
    }
}}
object ChThread{
    @JvmStatic val property = Prop
    @JvmStatic val msgType= MsgType::class.sealedSubclasses.map {
        val v = it.objectInstance as MsgType
        v.idx to v
    }.toMap()
    @JvmStatic private val mainHnd = object:Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg:Message){
            msgType[msg.what]?.f(msg.obj)
            msg.recycle()
        }
    }
    @JvmStatic private val que = Executors.newSingleThreadExecutor()
    @JvmStatic private val pool = Executors.newFixedThreadPool(3)
    @JvmStatic fun flushAll(){
        que.shutdownNow()
        pool.shutdownNow()
        mainHnd.looper.quit()
    }
    @JvmStatic fun isMain(): Boolean = Looper.getMainLooper().thread === Thread.currentThread()
    @JvmStatic fun que(task: Runnable) {que.execute(task)}
    @JvmStatic fun pool(task: Runnable) {pool.execute(task)}
    @JvmStatic fun main(task: Runnable) {main(0, task)}
    @JvmStatic fun main(delay: Long, task: Runnable) {
        if (delay == 0L) {
            if (isMain()) task.run() else mainHnd.post(task)
        } else mainHnd.postDelayed(task, delay)
    }
    @JvmStatic fun mainCancel(task: Runnable) {mainHnd.removeCallbacks(task)}
    @JvmStatic fun msg(type: MsgType, v: Any) {msg(0, type, v)}
    @JvmStatic fun msg(ms: Long, type:MsgType, v: Any) {
        val msg = mainHnd.obtainMessage(type.idx, v)
        if (ms == 0L) {
            if (isMain()) mainHnd.handleMessage(msg) else mainHnd.sendMessage(msg)
        } else mainHnd.sendMessageDelayed(msg, ms)
    }
    @JvmStatic fun msgCancel(type: MsgType, v: Any) {mainHnd.removeMessages(type.idx, v)}
}