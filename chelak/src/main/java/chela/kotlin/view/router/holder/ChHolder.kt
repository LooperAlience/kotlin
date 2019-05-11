package chela.kotlin.view.router.holder

import chela.kotlin.Ch

abstract class ChHolder<T>{
    var id:Ch.Id? = null
    open fun createInit(base: ChHolderBase<T>, vararg arg:Any){}
    abstract fun create(base: ChHolderBase<T>, vararg arg:Any):T

    open fun addPush(){}
    open fun addPop(){}
    open fun addRestore(){}

    open fun removePush(){}
    open fun removePop() = 0L
    open fun removeTake(){}

    open fun pauseRestore(){}
    open fun pausePush(){}

    open fun resume(){}

    open fun action(key: String, arg: Array<out Any>) = false
}