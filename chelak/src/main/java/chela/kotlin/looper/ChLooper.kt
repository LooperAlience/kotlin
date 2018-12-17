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

typealias ItemBlock = (ChItem)->Unit
typealias Now = ()->Double
internal val empty: ItemBlock = {}

internal val now:Now = { SystemClock.uptimeMillis().toDouble()}
private class Ani(ctx: Context, val looper: ChLooper): View(ctx), LifecycleObserver{
    init{tag = "CHELA_ANI"}
    override fun onDraw(canvas: Canvas?){
        looper.loop()
        invalidate()
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() = looper.resume()
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() = looper.pause()
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stop()=looper.clear()
}
class Sequence internal constructor(private val looper: ChLooper){
    internal lateinit var item: ChItem
    fun next(vararg param: ChLooper.Item, block: ItemBlock): Sequence {
        val i = looper.getItem(*param, block = block)
        item.next = i
        item = i
        return this
    }
}
class ChLooper{
    sealed class Item{
        class Time(val ms:Int): Item()
        class Delay(val ms:Int): Item()
        class Loop(val cnt:Int): Item()
        class Ended(val block:(ChItem)->Unit): Item()
    }
    private val sequence = Sequence(this)
    private var fps = 0.0
    private var previus = 0.0
    private var pauseStart = 0.0
    private var pausedTime = 0.0
    private val items = mutableListOf<ChItem>()
    private val itemPool = mutableListOf<ChItem>()

    fun act(act: AppCompatActivity){
        val root = act.window.decorView as ViewGroup
        if(root.findViewWithTag<Ani>("CHELA_ANI") == null){
            val ani = Ani(act, this)
            root.addView(ani)
            act.lifecycle.addObserver(ani)
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
        for(idx in items.size - 1..0){
            val item = items[idx]
            if(item.isPaused || item.start > c) break
            item.isTurn = false
            isEnd = false
            if(item.end <= c){
                item.loop--
                if(item.loop == 0){
                    rate = 1.0
                    isEnd = true
                }else{
                    rate = 0.0
                    item.isTurn = true
                    item.start = c
                    item.end = c + item.term
                }
            }else{
                rate = if(item.term == 0.0) 0.0 else (c - item.start) / item.term
            }
            item.rate = rate
            item.current = c
            item.isStop = false
            item.block(item)
            if(item.isStop || isEnd){
                item.ended(item)
                item.next?.let{
                    it.start += c
                    it.end = it.start + it.term
                    items += it
                }
                items -= item
                itemPool += item
            }
        }
    }
    fun add(vararg param: Item, block: ItemBlock): Sequence {
        val item = getItem(*param, block = block)
        item.start += now()
        item.end = item.start + item.term
        items += item
        sequence.item = item
        return sequence
    }
    internal fun getItem(vararg param: Item, block: ItemBlock): ChItem {
        val item = itemPool._shift() ?: ChItem()
        item.term = 0.0
        item.start = 0.0
        item.loop = 1
        item.block = block
        item.ended = empty
        item.next = null
        param.forEach{
            when(it){
                is Item.Time -> item.term = it.ms.toDouble()
                is Item.Delay -> item.start = it.ms.toDouble()
                is Item.Loop -> item.loop = it.cnt
                is Item.Ended -> item.ended = it.block
            }
        }
        return item
    }
    internal fun clear(){
        itemPool += items
        items.clear()
    }
    internal fun pause(){
        if(pauseStart != 0.0) pauseStart = now()
    }
    internal fun resume(){
        if(pauseStart != 0.0){
            pausedTime += now() - pauseStart
            pauseStart = 0.0
        }
    }
}