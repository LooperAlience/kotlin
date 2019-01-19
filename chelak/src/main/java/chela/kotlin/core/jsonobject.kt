package chela.kotlin.core

import org.json.JSONArray
import org.json.JSONObject

inline fun <T> JSONArray._for(block:(i:Int, v:T)->Unit) = (0 until this.length()).forEach{idx->
    try{
        @Suppress("UNCHECKED_CAST")
        (this._get(idx) as? T)?.let{block(idx, it)}
    }catch(e:Throwable){}
}
fun<T> JSONArray._toList():List<T>? = _try{(0 until this.length()).map{
    @Suppress("UNCHECKED_CAST")
    this[it] as T
}}
fun JSONArray._get(idx:Int) = _try{this[idx]}

inline fun JSONObject._for(key:String? = null, block:(key:String, v:Any)->Unit) = _try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->block(k, t.get(k))}
}
inline fun JSONObject._forObject(key:String? = null, block:(key:String, obj:JSONObject)->Unit) = _try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->t._object(k)?.let{block(k, it)}}
}
inline fun <T> JSONObject._forList(key:String? = null, block:(key:String, list:List<T>)->Unit) = _try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->t._array(k)?._toList<T>()?.let{block(k, it)}}
}
inline fun JSONObject._forString(key:String? = null, block:(key:String, v:String)->Unit) = _try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->t._string(k)?.let{block(k, it)}}
}
inline fun JSONObject._forValue(key:String? = null, block:(key:String, v:Any)->Unit) = _try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->
        val v = t.get(k)
        when(v){is String, is Number, is Boolean -> block(k, v)}
    }
}
inline fun<T> JSONObject._map(block:(Any)->T?):Map<String, T>? = _try{
    val map = mutableMapOf<String, T>()
    this._for{key, v->block(v)?.let{map[key] = it}}
    map
}
inline fun<T> JSONObject._mapObject(block:(JSONObject)->T?):Map<String, T>? = _try{
    val map = mutableMapOf<String, T>()
    this._forObject{key, v->block(v)?.let{map[key] = it}}
    map
}
fun<T:Comparable<*>> JSONObject._toMap():Map<String, T>? = _try{
    val map = mutableMapOf<String, T>()
    this._forValue{key, v->
        @Suppress("UNCHECKED_CAST")
        map[key] = v as T
    }
    map
}
fun<T> JSONObject._list(vararg key:String):List<T>?{
    var r:List<T>? = null
    key.any{r = _try{this.getJSONArray(it)._toList<T>()};r != null}
    return r
}
fun JSONObject._get(vararg key:String):Any?{
    var r:Any? = null
    key.any{r = _try{this.get(it)};r != null}
    return r
}
fun JSONObject._object(vararg key:String):JSONObject?{
    var r:JSONObject? = null
    key.any{r = _try{this.getJSONObject(it)};r != null}
    return r
}
fun JSONObject._array(vararg key:String):JSONArray?{
    var r:JSONArray? = null
    key.any{r = _try{this.getJSONArray(it)};r != null}
    return r
}
fun JSONObject._string(vararg key:String):String?{
    var r:String? = null
    key.any{r = _try{this.getString(it)};r != null}
    return r
}
fun JSONObject._int(vararg key:String):Int? {
    var r:Int? = null
    key.any{r = _try{this.getInt(it)};r != null}
    return r
}
fun JSONObject._long(vararg key:String):Long?{
    var r:Long? = null
    key.any{r = _try{this.getLong(it)};r != null}
    return r
}
fun JSONObject._float(vararg key:String):Float?{
    var r:Float? = null
    key.any{r = _try{this.getDouble(it).toFloat()};r != null}
    return r
}
fun JSONObject._double(vararg key:String):Double?{
    var r:Double? = null
    key.any{r = _try{this.getDouble(it)};r != null}
    return r
}
fun JSONObject._boolean(vararg key:String):Boolean?{
    var r:Boolean? = null
    key.any {r = _try { this.getBoolean(it)};r != null}
    return r
}