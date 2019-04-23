package chela.kotlin.view.router.holder

import chela.kotlin.thread.ChThread
import chela.kotlin.view.router.ChRouter

abstract class ChHolderBase<T>{
    internal fun _push(holder: ChHolder<T>, isRestore:Boolean){
        push(holder, isRestore)
        holder.push(this, isRestore)
    }
    internal fun _pop(holder: ChHolder<T>, isJump:Boolean){
        ChThread.main(holder.pop(this, isJump), Runnable{pop(holder, isJump)})
    }
    internal fun _pause(holder: ChHolder<T>, isJump:Boolean){
        pause(holder, isJump)
        holder.pause(this, isJump)
    }
    internal fun _resume(holder: ChHolder<T>, isRestore:Boolean){
        resume(holder, isRestore)
        holder.resume(this, isRestore)
    }
    internal fun _take(index:Int, holder: ChHolder<T>){
        take(index, holder)
        holder.take(this)
    }
    protected open fun push(holder:ChHolder<T>, isRestore:Boolean){}
    open fun pop(holder: ChHolder<T>, isJump:Boolean){}
    protected open fun pause(holder: ChHolder<T>, isJump:Boolean){}
    protected open fun resume(holder: ChHolder<T>, isRestore:Boolean){}
    open fun take(index:Int, holder: ChHolder<T>){}
    open fun clear(){}
}