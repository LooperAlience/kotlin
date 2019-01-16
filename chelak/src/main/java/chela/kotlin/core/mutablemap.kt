package chela.kotlin.core

interface toJSON{fun toJSON():String}
fun <K, V> Map<K, V>._toJSON():String{
    val r = mutableListOf<String>()
    this.forEach{(k, v)->
        r += when(v) {
            is toJSON->v.toJSON()
            is Map<*, *>->v._toJSON()
            else->{
                @Suppress("IMPLICIT_CAST_TO_ANY")
                "\"$k\":${if (v is String) "\"${v.replace("\"", "\\\"")}\"" else v}"
            }
        }
    }
    return "{" + r.joinToString(",") + "}"
}