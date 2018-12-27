package chela.kotlin.core

fun String._shift():String  = this.substring(1)
fun String._pop():String  = this.substring(0, this.length - 1)
fun String._firstUpper():String  = this[0].toUpperCase() + this.substring(1)
fun String._firstLower():String  = this[0].toLowerCase() + this.substring(1)

inline fun String._notBlank(block:(String)->Unit){
    val v = this.trim()
    if(v.isNotBlank()) block(v)
}