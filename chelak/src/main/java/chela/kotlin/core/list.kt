package chela.kotlin.core

fun List<Pair<String, Any>>?._toString():String = this?.map{(k, v)->"$k:$v"}?.joinToString(",") ?: ""
fun List<Pair<String, Any>>._stringify():String = """{
    ${this.joinToString(","){(k, v)->
        " \"$k\" : ${if(v is String) "\"${v.replace("\"", "\\\"")}\"" else v}"
    }}
}"""