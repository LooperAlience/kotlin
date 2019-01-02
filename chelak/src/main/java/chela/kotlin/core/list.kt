package chela.kotlin.core

fun List<Pair<String, Any>>?._toString():String = this?.map{(k, v)->"$k:$v"}?.joinToString(",") ?: ""