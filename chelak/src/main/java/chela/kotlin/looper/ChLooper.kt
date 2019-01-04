package chela.kotlin.looper

import android.content.Context
import android.graphics.Canvas
import android.os.SystemClock
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

/**
 * This class executes UI update about its Item type on main thread.
 * Asynchronous execution is also possible.
 * <pre>
 *   App.looper(Ch.infinity()) { item ->
 *      ...
 *   }
 * </pre>
 */
typealias ItemBlock = (ChItem)->Unit
typealias Now = ()->Double
internal val empty: ItemBlock = {}
internal val now:Now = { SystemClock.uptimeMillis().toDouble()}
class ChLooper:LifecycleObserver{
    sealed class Item{
        class Time(val ms:Int): Item()
        class Delay(val ms:Int): Item()
        class Loop(val cnt:Int): Item()
        class Ended(val block:(ChItem)->Unit): Item()
        class Infinity:Item()
    }
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
    class Sequence internal constructor(private val looper: ChLooper){
        internal lateinit var item: ChItem
        fun next(vararg param: ChLooper.Item, block: ItemBlock = empty): Sequence {
            val i = looper.getItem(*param, block = block)
            item.next = i
            item = i
            return this
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
    /**
     * set Looper
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
        var isEnd = false
        var rate = 0.0
        if(items.isEmpty()) return
        remove.clear()
        add.clear()
        lock.read {
            var i = 0
            while(i < items.size) {
                val item = items[i++]
                if (item.isPaused || item.start > c) break
                item.isTurn = false
                isEnd = false
                if(!item.isInfinity && item.end <= c){
                    item.loop--
                    if (item.loop == 0) {
                        rate = 1.0
                        isEnd = true
                    } else {
                        rate = 0.0
                        item.isTurn = true
                        item.start = c
                        item.end = c + item.term
                    }
                }else{
                    rate = if (item.term == 0.0) 0.0 else (c - item.start) / item.term
                }
                item.rate = rate
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
    operator fun invoke(vararg param: Item, block: ItemBlock = empty): Sequence {
        val item = getItem(*param, block = block)
        item.start += now()
        item.end = if(item.term == -1.0) -1.0 else item.start + item.term
        lock.write {items += item}
        sequence.item = item
        return sequence
    }
    internal fun getItem(vararg param: Item, block: ItemBlock): ChItem {
        val item = itemPool._shift() ?: ChItem()
        item.term = -1.0
        item.start = 0.0
        item.loop = 1
        item.block = block
        item.ended = empty
        item.next = null
        item.isInfinity = false
        param.forEach{
            when(it){
                is Item.Infinity -> item.isInfinity = true
                is Item.Time -> item.term = it.ms.toDouble()
                is Item.Delay -> item.start = it.ms.toDouble()
                is Item.Loop -> item.loop = it.cnt
                is Item.Ended -> item.ended = it.block
            }
        }
        return item
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