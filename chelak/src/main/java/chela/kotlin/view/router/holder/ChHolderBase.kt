package chela.kotlin.view.router.holder

import chela.kotlin.Ch
import chela.kotlin.thread.ChThread

abstract class ChHolderBase<T>{
    protected var id = Ch.Id()
    fun checkId(v:Ch.Id?):Ch.Id? = if(id === v) null else id

    internal fun _push(holder: ChHolder<T>, isRestore:Boolean){
        push(holder, isRestore)
        holder.push(isRestore)
    }
    internal fun _pop(holder: ChHolder<T>, isRestore:Boolean){
        ChThread.main(holder.pop(isRestore), Runnable{pop(holder, isRestore)})
    }
    internal fun _pause(holder: ChHolder<T>, isRestore:Boolean){
        pause(holder, isRestore)
        holder.pause(isRestore)
    }
    internal fun _resume(holder: ChHolder<T>, isRestore:Boolean){
        resume(holder, isRestore)
        holder.resume(isRestore)
    }
    internal fun _take(holder: ChHolder<T>){
        take(holder)
        holder.take()
    }
    protected open fun push(holder:ChHolder<T>, isRestore:Boolean){}
    open fun pop(holder: ChHolder<T>, isRestore:Boolean){}
    protected open fun pause(holder: ChHolder<T>, isRestore:Boolean){}
    protected open fun resume(holder: ChHolder<T>, isRestore:Boolean){}
    open fun take(holder: ChHolder<T>){}
    open fun clear(){}
}