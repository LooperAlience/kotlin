package chela.kotlin.view.router.holder

abstract class ChHolder<T>(val name:String = ""){
    abstract fun create(base: ChHolderBase<T>, isRestore:Boolean, vararg arg:Any):T
    open fun push(isRestore:Boolean){}
    open fun pop(isRestore:Boolean) = 0L
    open fun resume(isRestore:Boolean){}
    open fun pause(isRestore:Boolean){}
    open fun take(){}
    open fun action(key: String, arg: Array<out Any>) = false
}