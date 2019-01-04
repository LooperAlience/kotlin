package chela.kotlin.core

inline fun <T> _try(block:()->T) = try{block()}catch(e:Throwable){null}
inline fun _isNotNull(vararg v:Any?, block:()->Unit){
    if(v.all { it != null }) block()
}