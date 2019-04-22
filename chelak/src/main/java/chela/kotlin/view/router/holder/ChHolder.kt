package chela.kotlin.view.router.holder

import chela.kotlin.thread.ChThread
import chela.kotlin.view.router.ChRouter

abstract class ChHolderBase<T>{
    internal lateinit var router: ChRouter<T>
    internal fun _push(holder: ChHolder<T>, isRestore:Boolean){
        push(holder)
        holder.push(this, isRestore)
    }
    internal fun _pop(holder: ChHolder<T>, isJump:Boolean){
        ChThread.main(holder.pop(this, isJump), Runnable {pop(holder)})
    }
    internal fun _pause(holder: ChHolder<T>, isJump:Boolean){
        pause(holder)
        holder.pause(this, isJump)
    }
    internal fun _resume(holder: ChHolder<T>, isRestore:Boolean){
        resume(holder)
        holder.resume(this, isRestore)
    }
    internal fun _take(index:Int, holder: ChHolder<T>){
        take(index, holder)
        holder.take(this)
    }
    protected fun restore(){router.restore()}
    protected open fun push(holder: ChHolder<T>){}
    open fun pop(holder: ChHolder<T>){}
    protected open fun pause(holder: ChHolder<T>){}
    protected open fun resume(holder: ChHolder<T>){}
    open fun take(index:Int, holder: ChHolder<T>){}
    open fun clear(){}
}
abstract class ChHolder<T>(val isJumpPoint:Boolean = false){
    abstract fun create(base: ChHolderBase<T>):T
    open fun push(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun pop(base: ChHolderBase<T>, isJump:Boolean) = 0L
    open fun resume(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun pause(base: ChHolderBase<T>, isJump:Boolean){}
    open fun take(base: ChHolderBase<T>){}
    open fun action(key: String, arg: Array<out Any>) = false
}