package chela.kotlin.core

internal fun <T> MutableList<T>._shift():T? = if(this.isNotEmpty()) this.removeAt(0) else null
internal fun <T> MutableList<T>._pop():T = this.removeAt(this.size - 1)
internal inline fun <T> MutableList<T>._notEmpty(block:(MutableList<T>)->Unit){
    if(this.isNotEmpty()) block(this)
}
inline fun <T> MutableList<T>._allStack(block:(T, MutableList<T>)->Boolean){
    while(this.isNotEmpty()) if(!block(this.removeAt(this.size - 1), this)) break
}
inline fun <T> MutableList<T>._cutStack(block:(T, MutableList<T>)->Boolean){
    var i = this.size - 1
    while(i-- > 0){
        if(!block(this[i], this)) this.removeAt(i)
    }
}
