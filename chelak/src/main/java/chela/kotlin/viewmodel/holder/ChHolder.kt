package chela.kotlin.viewmodel.holder

import chela.kotlin.viewmodel.ChRouter

abstract class ChHolderBase<T>{
    internal lateinit var router: ChRouter<T>
    internal fun _push(holder: ChHolder<T>, isRestore:Boolean){
        push(holder)
        holder.push(this, isRestore)
        _resume(holder, isRestore)
    }
    internal fun _pop(holder: ChHolder<T>, isJump:Boolean){
        _pause(holder, isJump)
        if(holder.pop(this, isJump)) pop(holder)
    }
    internal fun _pause(holder: ChHolder<T>, isJump:Boolean){
        pause(holder)
        holder.pause(this, isJump)
    }
    internal fun _resume(holder: ChHolder<T>, isRestore:Boolean){
        resume(holder)
        holder.resume(this, isRestore)
    }
    protected fun restore(){router.restore()}
    protected open fun push(holder: ChHolder<T>){}
    open fun pop(holder: ChHolder<T>){}
    protected open fun pause(holder: ChHolder<T>){}
    protected open fun resume(holder: ChHolder<T>){}
}
abstract class ChHolder<T>{
    @JvmField internal var key:String = ""
    init{
        @Suppress("LeakingThis")
        this::class.simpleName?.let{key = it}
    }
    abstract fun create(base: ChHolderBase<T>):T
    open fun push(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun resume(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun pause(base: ChHolderBase<T>, isJump:Boolean){}
    open fun pop(base: ChHolderBase<T>, isJump:Boolean):Boolean = true
}