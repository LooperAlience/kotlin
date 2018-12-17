package chela.kotlin.viewmodel

import chela.kotlin.core._allStack
import chela.kotlin.core._pop
import chela.kotlin.viewmodel.holder.ChHolder
import chela.kotlin.viewmodel.holder.ChHolderBase

class ChRouter<T>(private val base: ChHolderBase<T>){
    private val stack = mutableListOf<ChHolder<T>>()
    private var pushLock = false
    private var popLock = false
    init{base.router = this}
    fun restore(){stack.forEach{base._push(it, true)}}
    fun unlockPush(){if(pushLock) pushLock = false}
    fun unlockPop(){if(popLock) popLock = false}
    fun push(holder: ChHolder<T>, isAutoUnlock:Boolean = true){
        if(pushLock) return
        if(!isAutoUnlock) pushLock = true
        if(stack.isNotEmpty()) base._pause(stack.last(), false)
        base._push(holder, false)
        stack += holder
    }
    fun pop(isAutoUnlock:Boolean = true){
        if(popLock || stack.isEmpty()) return
        if(!isAutoUnlock) popLock = true
        val h = stack.last()
        base._pop(h, false)
        stack._pop()
        if(stack.isNotEmpty()) base._resume(stack.last(), false)
    }
    fun jump(key:String) = stack._allStack { holder, _ ->
        if(holder.key == key){
            base._resume(holder, false)
            false
        }else{
            base._pop(holder, true)
            true
        }
    }
    fun url(url:String){}
}