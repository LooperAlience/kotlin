package chela.kotlin.view.router.holder

abstract class ChHolder<T>(val isJumpPoint:Boolean = false){
    abstract fun create(base: ChHolderBase<T>, isRestore:Boolean, vararg arg:Any):T
    open fun push(isRestore:Boolean){}
    open fun pop(isJump:Boolean) = 0L
    open fun resume(isRestore:Boolean){}
    open fun pause(isJump:Boolean){}
    open fun take(){}
    open fun action(key: String, arg: Array<out Any>) = false
}