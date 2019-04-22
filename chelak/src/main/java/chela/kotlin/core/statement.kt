package chela.kotlin.core

inline fun <T> _try(block:()->T) = try{block()}catch(e:Throwable){null}
inline fun <T> notNull(vararg v:T?, block:(List<T>)->Unit) = run{
    val r = mutableListOf<T>()
    if(v.all {
        it?.let{r += it}
        it != null
    }){
        block(r)
        true
    }else null
}