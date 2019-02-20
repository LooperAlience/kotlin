package chela.kotlin.looper

import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import chela.kotlin.core._shift
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.util.concurrent.locks.ReentrantReadWriteLock

typealias ItemBlock = (ChItem)->Unit
typealias Now = ()->Double
/**
 * This class executes UI upgrade about its ItemDSL type on main thread.
 * Asynchronous execution is also possible.
 * <pre>
 *   App.looper(Ch.infinity()) { item ->
 *      ...
 *   }
 * </pre>
 */
class ChLooper:LifecycleObserver{
    /**
     * Makes ChLooper run on the main thread
     * @param ctx activity context
     * @param looper its loop() execute on main thread
     */
    private class Ani(ctx: Context, private val looper: ChLooper): View(ctx){
        init{tag = "CHELA_ANI"}
        override fun onDraw(canvas: Canvas?){
            looper.loop()
            invalidate()
        }
    }
    private val sequence = Sequence(this)
    private var fps = 0.0
    private var previus = 0.0
    private var pauseStart = 0.0
    private var pausedTime = 0.0
    private val items = mutableListOf<ChItem>()
    private val remove = mutableListOf<ChItem>()
    private val add = mutableListOf<ChItem>()
    private val itemPool = mutableListOf<ChItem>()
    private val lock =  ReentrantReadWriteLock()

    operator fun invoke(block:ItemDSL.()->Unit):Sequence{
        val item = getItem(ItemDSL().apply{block()})
        item.start += now()
        item.end = if(item.term == -1.0) -1.0 else item.start + item.term
        lock.write {items += item}
        sequence.item = item
        return sequence
    }
    internal fun getItem(i:ItemDSL):ChItem = (itemPool._shift() ?: ChItem()).also{ item->
        with(i){
            item.term = time.toDouble()
            item.start = delay.toDouble()
            item.loop = loop
            item.block = block
            item.ended = ended
            item.isInfinity = isInfinity
            item.next = null
        }
    }
    /**
     * add Looper
     * @param act activity context
     */
    fun act(act: AppCompatActivity){
        val root = act.window.decorView as ViewGroup
        if(root.findViewWithTag<Ani>("CHELA_ANI") == null){
            val ani = Ani(act, this)
            root.addView(ani)
            act.lifecycle.addObserver(this)
        }
    }
    fun loop(){
        val c = now()
        val gap = c - previus
        if(gap > 0.0) fps = 1000.0 / gap
        previus = c
        if(items.isEmpty()) return
        remove.clear()
        add.clear()
        lock.read {
            var i = 0
            while(i < items.size) {
                val item = items[i++]
                if (item.isPaused || item.start > c) break
                item.isTurn = false
                var isEnd = false
                item.rate = if(!item.isInfinity && item.end <= c){
                    item.loop--
                    if (item.loop == 0) {
                        isEnd = true
                        1.0
                    } else {
                        item.isTurn = true
                        item.start = c
                        item.end = c + item.term
                        0.0
                    }
                }else if (item.term == 0.0) 0.0
                else (c - item.start) / item.term
                item.current = c
                item.isStop = false
                item.block(item)
                if (item.isStop || isEnd) {
                    item.ended(item)
                    item.next?.let {
                        it.start += c
                        it.end = it.start + it.term
                        add += it
                    }
                    remove += item
                }
            }
        }
        if(remove.isNotEmpty() || add.isNotEmpty()) lock.write {
            if(remove.isNotEmpty()){
                items -= remove
                itemPool += remove
            }
            if(add.isNotEmpty()) items += add
        }
    }
    /**
     * LifecycleObserver detect life cycle event.
     *
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clear(){
        itemPool += items
        items.clear()
    }
    /**
     * It handle time offset between pause and resume.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun pause(){
        if(pauseStart != 0.0) pauseStart = now()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun resume(){
        if(pauseStart != 0.0){
            pausedTime += now() - pauseStart
            pauseStart = 0.0
        }
    }
}