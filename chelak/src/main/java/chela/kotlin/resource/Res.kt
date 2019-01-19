package chela.kotlin.resource

import chela.kotlin.core.*
import chela.kotlin.i18n.ChI18n
import chela.kotlin.net.ChNet
import chela.kotlin.sql.ChSql
import org.json.JSONArray
import org.json.JSONObject

class Res internal constructor(internal var id:String = "", v:JSONObject):toJSON{
    private val json = "$v"
    private var ruleset:Map<String, Ruleset>? = null
    private var query:Map<String, String>? = null
    private var db:Map<String, Db>? = null
    private var api:Map<String, Api>? = null
    private var style:Map<String, Style>? = null
    private var shape:Map<String, Shape>? = null
    private var i18n:Map<String, I18n>? = null
    init{
        v._string("id")?.let{id = it}
        v._forObject {key, it->
            when(key){
                "db"->db = it._mapObject{Db(it)}
                "api"->api = it._mapObject{Api(it, it._string("base") ?: "")}
                "style"->style = it._mapObject{Style(it)}
                "shape"->shape = it._mapObject{Shape(it)}
                "ruleset"->ruleset = it._mapObject{Ruleset(it)}
                "i18n"->{
                    it._string("language")?.let{ChI18n(it)}
                    i18n = it._mapObject{I18n(it)}
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
        i18n?.forEach{(k,v)->v.remove(k)}
        style?.forEach{(k,v)->v.remove(k)}
        shape?.forEach{(k,v)->v.remove(k)}
        api?.forEach{(k,v)->v.remove(k)}
        ruleset?.forEach{(k,v)->v.remove(k)}
        query?.forEach{(k,_)->ChSql.removeQuery(k)}
        db?.forEach{(k,v)->v.remove(k)}
    }
    fun set(){
        ruleset?.forEach { (k, v) -> v.set(k) }
        query?.forEach { (k, v) -> ChSql.addQuery(k, v) }
        db?.forEach { (k, v) -> v.set(k) }
        api?.forEach{(k, v)->v.set(k)}
        style?.forEach{(k, v)->v.set(k)}
        shape?.forEach{(k, v)->v.set(k)}
        i18n?.forEach{(k, v)->v.set(k)}
    }
    override fun toJSON():String = json
}