package chela.kotlin.thread

import android.os.Handler
import android.os.Looper
import android.os.Message
import chela.kotlin.viewmodel.scanner.ChScannedItem
import chela.kotlin.viewmodel.properties
import java.util.concurrent.Executors

val threadUtil = ChThread()

class ChThread{
    enum class MsgType{
        Prop{override fun f(it: Any){
            if(it !is Set<*>) return
            it.forEach {
                if(it !is ChScannedItem) return
                val view = it.view
                it.collector.forEach{(k, v)-> properties[k.toLowerCase()]?.f(view, v)}
            }
        }};
        internal abstract fun f(it:Any)
    }
    private val mainHnd = object : Handler(Looper.getMainLooper()) {
        private val msgTypes = MsgType.values()
        override fun handleMessage(msg:Message){
            val idx = msg.what
            if(idx < msgTypes.size) msgTypes[idx].f(msg.obj)
            msg.recycle()
        }
    }
    private val que = Executors.newSingleThreadExecutor()
    private val pool = Executors.newFixedThreadPool(3)
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
    fun msg(ms: Long, type: MsgType, v: Any) {
        val msg = mainHnd.obtainMessage(type.ordinal, v)
        if (ms == 0L) {
            if (isMain()) mainHnd.handleMessage(msg) else mainHnd.sendMessage(msg)
        } else mainHnd.sendMessageDelayed(msg, ms)
    }
    fun msgCancel(type: MsgType, v: Any) {mainHnd.removeMessages(type.ordinal, v)}
}