package chela.kotlin.core

import org.json.JSONArray
import org.json.JSONObject

inline fun JSONObject._keys(key:String? = null, block:(key:String, obj:JSONObject)->Unit) = try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->block(k, t)}
}catch(e:Throwable){}
inline fun JSONObject._forObject(key:String? = null, block:(key:String, obj:JSONObject)->Unit) = try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->t._object(k)?.let{block(k, it)}}
}catch(e:Throwable){}
inline fun JSONObject._forString(key:String? = null, block:(key:String, v:String)->Unit) = try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->t._string(k)?.let{block(k, it)}}
}catch(e:Throwable){}
inline fun JSONObject._forValue(key:String? = null, block:(key:String, v:Any)->Unit) = try{
    val t = if(key != null) this.getJSONObject(key) else this
    t.keys().forEach{k->
        val v = t.get(k)
        when(v){is String, is Number, is Boolean -> block(k, v)}
    }
}catch(e:Throwable){}
@Suppress("UNCHECKED_CAST")
fun<T> JSONArray._toList():List<T> = (0 until this.length()).map{this[it] as T}
fun<T> JSONObject._list(key:String) = try{
    this.getJSONArray(key)._toList<T>()
}catch(e:Throwable){
    null
}
fun JSONObject._get(key:String) = try{this.get(key)}catch(e:Throwable){null}
fun JSONObject._object(key:String) = try{this.getJSONObject(key)}catch(e:Throwable){null}
fun JSONObject._array(key:String) = try{this.getJSONArray(key)}catch(e:Throwable){null}
fun JSONObject._string(key:String) = try{this.getString(key)}catch(e:Throwable){null}
fun JSONObject._int(key:String) = try{this.getInt(key)}catch(e:Throwable){null}
fun JSONObject._long(key:String) = try{this.getLong(key)}catch(e:Throwable){null}
fun JSONObject._float(key:String) = try{this.getDouble(key).toFloat()}catch(e:Throwable){null}
fun JSONObject._double(key:String) = try{this.getDouble(key)}catch(e:Throwable){null}
fun JSONObject._boolean(key:String) = try{this.getBoolean(key)}catch(e:Throwable){null}