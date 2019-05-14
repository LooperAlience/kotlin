package chela.kotlin.view.router

import chela.kotlin.core._pop
import chela.kotlin.thread.ChThread
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase

class ChRouter<T, R:ChHolderBase<T>>(val base:R){
    enum class State{RESTORE, POP, PUSH, TAKE}
    private val stack = mutableListOf<ChHolder<T>>()
    private var isRestored = false
    fun restore() = if(stack.isEmpty()) false
    else{
        isRestored = true
        base.clear()
        val s = stack.size
        if(s > 1){
            val h = stack[s - 2]
            base._add(h, State.RESTORE)
            base._pause(h, State.RESTORE)
        }
        base._add(stack[s - 1], State.RESTORE)
        true
    }
    fun push(holder:ChHolder<T>) = ChThread.main(Runnable{
        if(stack.isNotEmpty()) base._pause(stack.last(), State.PUSH)
        stack += holder
        base._add(holder, State.PUSH)
        val s = stack.size
        if(s > 2){
            val h = stack[s - 3]
            base._remove(h, State.PUSH)
        }
    })
    fun pop():Int{
        if(stack.isEmpty()) return 0
        ChThread.main(Runnable{
            base._remove(stack._pop(), State.POP)
            if(stack.isNotEmpty()) base._resume(stack.last())
            val s = stack.size
            if(s > 1){
                val h = stack[s - 2]
                base._add(h, State.POP)
            }

        })
        return stack.size
    }
    fun take(holder:ChHolder<T>){
        val i = stack.lastIndexOf(holder)
        if(i != -1){
            stack.removeAt(i)
            ChThread.main(Runnable {base._remove(holder, State.TAKE)})
        }
    }
    fun takeBeforeLast(){
        var i = stack.size - 1
        while(i-- > 0){
            val h = stack.removeAt(i)
            ChThread.main(Runnable {base._remove(h, State.TAKE)})
        }
    }
    fun clear() {
        stack.clear()
        base._clear()
    }
    fun findHolder(block:(ChHolder<T>)->Boolean):ChHolder<T>? = stack.find(block)
    fun action(key:String, vararg arg:Any) = if(stack.isNotEmpty()) stack.last().action(key, arg) else false
}