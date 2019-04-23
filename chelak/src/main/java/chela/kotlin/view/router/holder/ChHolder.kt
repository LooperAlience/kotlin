package chela.kotlin.view.router.holder

abstract class ChHolder<T>(val isJumpPoint:Boolean = false){
    abstract fun create(base: ChHolderBase<T>, isRestore:Boolean, vararg arg:Any):T
    open fun push(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun pop(base: ChHolderBase<T>, isJump:Boolean) = 0L
    open fun resume(base: ChHolderBase<T>, isRestore:Boolean){}
    open fun pause(base: ChHolderBase<T>, isJump:Boolean){}
    open fun take(base: ChHolderBase<T>){}
    open fun action(key: String, arg: Array<out Any>) = false
}