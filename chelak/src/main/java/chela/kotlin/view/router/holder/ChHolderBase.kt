package chela.kotlin.view.router.holder

import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.thread.ChThread
import chela.kotlin.view.router.ChRouter

abstract class ChHolderBase<T>{
    private val holderT = mutableMapOf<ChHolder<T>, T>()
    protected var id = Ch.Id()
    protected fun newId(){id = Ch.Id()}
    protected fun create(holder:ChHolder<T>, vararg arg:Any):T{
        if(id !== holder.id){
            holder.id = id
            holder.createInit(this, *arg)
            holderT.remove(holder)
        }
        val r = holderT[holder] ?: holder.create(this, *arg)
        holderT[holder] = r
        return r
    }
    protected fun T(holder: ChHolder<T>) = holderT[holder]
    internal fun _add(holder: ChHolder<T>, state: ChRouter.State){
        add(holder, state == ChRouter.State.POP)
        when(state){
            ChRouter.State.PUSH ->holder.addPush()
            ChRouter.State.POP ->holder.addPop()
            ChRouter.State.RESTORE ->holder.addRestore()
        }
    }
    internal fun _remove(holder: ChHolder<T>, state:ChRouter.State) = holderT[holder]?.let {
        holderT -= holder
        var r = 0L
        when(state){
            ChRouter.State.PUSH ->holder.removePush()
            ChRouter.State.TAKE ->holder.removeTake()
            ChRouter.State.POP ->r = holder.removePop()
        }
        ChThread.main(r, Runnable{remove(holder, it)})
    }
    internal fun _pause(holder: ChHolder<T>, state:ChRouter.State) = holderT[holder]?.let {
        pause(holder)
        when(state){
            ChRouter.State.RESTORE ->holder.pauseRestore()
            ChRouter.State.PUSH ->holder.pausePush()
        }
    }
    internal fun _resume(holder: ChHolder<T>) = holderT[holder]?.let {
        resume(holder)
        holder.resume()
    }
    internal fun _clear(){
        holderT.clear()
        clear()
    }
    open fun add(holder:ChHolder<T>, isBottom:Boolean){}
    open fun remove(holder: ChHolder<T>, t:T){}
    protected open fun pause(holder: ChHolder<T>){}
    protected open fun resume(holder: ChHolder<T>){}
    open fun clear(){}
}