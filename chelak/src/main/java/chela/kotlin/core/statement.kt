package chela.kotlin.core

inline fun <T> _try(block:()->T) = try{block()}catch(e:Throwable){null}
inline fun _requiredNotNull(vararg v:Any?, block:()->Unit){
    if(v.all { it != null }) block()
}