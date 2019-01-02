package chela.kotlin.core

import org.json.JSONArray

@Suppress("UNCHECKED_CAST")
fun<T> JSONArray?.toList():List<T> = if(this != null) (0 until this.length()).map{this[it] as T} else listOf()
