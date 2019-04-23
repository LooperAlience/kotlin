package chela.kotlin.resource

import android.util.Log
import chela.kotlin.core.*
import chela.kotlin.cdata.ChCdata
import chela.kotlin.sql.ChSql
import org.json.JSONArray
import org.json.JSONObject

class Res internal constructor(internal var id:String = "", v:JSONObject):toJSON{
    private val json = "$v"
    private var ruleset:Map<String, Ruleset>? = null
    private var query:Map<String, String>? = null
    private var db:Map<String, Db>? = null
    private var api:Map<String, Api>? = null
    private var font:Map<String, Font>? = null
    private var style:Map<String, Style>? = null
    private var shape:Map<String, Shape>? = null
    private var cdata:Map<String, JSONObject>? = null
    private var cdataVal:Map<String, String>? = null
    init{
        v._string("id")?.let{id = it}
        v._forObject {key, it->
            when(key){
                "db"->db = it._mapObject{Db(it)}
                "api"->api = it._mapObject{Api(it, it._string("base") ?: "")}
                "font"->font = it._mapString{Font(it)}
                "style"->style = it._mapObject{Style(it)}
                "shape"->shape = it._mapObject{Shape(it)}
                "ruleset"->ruleset = it._mapObject{Ruleset(it)}
                "cdata"->{
                    val key = mutableMapOf<String, String>()
                    it._forString { k, v ->key[k] = v}
                    if(key.isNotEmpty()) cdataVal = key
                    val obj = mutableMapOf<String, JSONObject>()
                    it._forObject{k, v->obj[k] = v}
                    if(obj.isNotEmpty()) cdata = obj
                }
                "query"->query = it._map{
                    when(it){
                        is String->it
                        is JSONArray -> it._toList<String>()?.joinToString(" ")
                        else->null
                    }
                }
            }
        }
    }
    fun remove(){
        cdata?.forEach{ (k,v)->v.remove(k)}
        font?.forEach{(k,v)->v.remove(k)}
        style?.forEach{(k,v)->v.remove(k)}
        shape?.forEach{(k,v)->v.remove(k)}
        api?.forEach{(k,v)->v.remove(k)}
        ruleset?.forEach{(k,v)->v.remove(k)}
        query?.forEach{(k,_)->ChSql.removeQuery(k)}
        db?.forEach{(k,v)->v.remove(k)}
        cdataVal?.forEach{ (k, v) ->
            if(k.startsWith("@@")) ChCdata.catDefault.remove(k.substring(1))
            else ChCdata.cat.remove(k)
        }
        cdata?.forEach{(k, v)->ChCdata.root.remove(k)}
    }
    fun set(){
        api?.forEach{(k, v)->v.set(k)}
        ruleset?.forEach{(k, v)->v.set(k)}
        query?.forEach{(k, v)-> ChSql.addQuery(k, v)}
        font?.forEach{(k, v)->v.set(k)}
        style?.forEach{(k, v)->v.set(k)}
        shape?.forEach{(k, v)->v.set(k)}
        db?.forEach{(k, v)-> v.set(k)}
        cdataVal?.forEach{ (k, v) ->
            if(k.startsWith("@@")) ChCdata.catDefault[k.substring(1)] = v
            else ChCdata(k, v)
        }
        cdata?.forEach{(k, v)->Cdata(k, v)}
    }
    override fun toJSON():String = json
}