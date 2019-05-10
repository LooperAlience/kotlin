package chela.kotlin.view.router

import chela.kotlin.core._pop
import chela.kotlin.thread.ChThread
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase

class ChRouter<T, R:ChHolderBase<T>>(val base:R){
    private val stack = mutableListOf<ChHolder<T>>()
    val isFinal:Boolean get() = stack.size == 1
    var isRestored = false
    fun restore() = if(stack.isEmpty()) false
    else{
        isRestored = true
        base.clear()
        base._push(stack.last(), true)
        true
    }
    fun push(holder:ChHolder<T>) = ChThread.main(Runnable{
        if(stack.isNotEmpty()) base._pause(stack.last(), false)
        base._push(holder, false)
        stack += holder
    })
    fun pop():Int{
        if(stack.isEmpty()) return 0
        ChThread.main(Runnable{
            base._pop(stack.last(), false)
            stack._pop()
            if(stack.isNotEmpty()){
                val h = stack.last()
                if(isRestored) base._push(h, true)
                base._resume(h, true)
            }
        })
        return stack.size
    }
    fun take(holder: ChHolder<T>){
        val i = stack.lastIndexOf(holder)
        if(i != -1){
            stack.removeAt(i)
            ChThread.main(Runnable {base._take(holder)})
        }
    }
    fun take(name:String){
        var i = stack.size
        while(i-- > 0){
            if(stack[i].name == name){
                stack.removeAt(i)
                ChThread.main(Runnable {base._take(stack[i])})
                break
            }
        }
    }
    fun clear() {
        stack.clear()
        base.clear()
    }
    fun action(key:String, vararg arg:Any) = if(stack.isNotEmpty()) stack.last().action(key, arg) else false
}