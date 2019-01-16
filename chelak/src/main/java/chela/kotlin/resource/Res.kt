package chela.kotlin.resource

import chela.kotlin.core.*
import chela.kotlin.sql.ChSql
import chela.kotlin.validation.ChRuleSet
import org.json.JSONArray
import org.json.JSONObject

class Res(internal var id:String = "", v:JSONObject):toJSON {
    private var ruleset:Map<String, Ruleset>? = null
    private var query:Map<String, String>? = null
    private var db:Map<String, Db>? = null
    private var api:Map<String, Api>? = null
    private var style:Map<String, Style>? = null
    private var i18n:Map<String, I18n>? = null
    init{
        v._string("id")?.let{id = it}
        v._forObject {key, it->
            when(key){
                "db"->db = it._mapObject{Db(it)}
                "api"->api = it._mapObject{Api(it)}
                "i18n"->i18n = it._mapObject{I18n(it)}
                "style"->style = it._mapObject{Style(it)}
                "ruleset"->ruleset = it._mapObject{Ruleset(it)}
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
        api?.forEach{(k,v)->v.remove(k)}
        ruleset?.forEach{(k,v)->v.remove(k)}
        query?.forEach{(k,_)->ChSql.removeQuery(k)}
        db?.forEach{(k,v)->v.remove(k)}
    }
    fun setDb(){
        ruleset?.forEach{(k, v)->v.set(k)}
        query?.forEach{(k, v)->ChSql.addQuery(k, v)}
        db?.let{
            val cnt = it.size
            var i = 0
            var isDefault = false
            it.forEach{(k, v)->
                val isD = if(++i == cnt && !isDefault) true
                else{
                    if(v.isDefault) isDefault = true
                    v.isDefault
                }
                var c = v.create
                var u = v.upgrade
                if(isD){
                    val (cr, up) = ChRes.base()
                    c = "$cr,$c"
                    u = "$up,$u"
                }
                ChSql.addDb(k, v.ver, c, u, isD)
                if(isD) ChRes.baseQuery()
            }
        }
    }
    fun setRes(){
        api?.forEach{(k, v)->

        }
        style?.forEach{(k, v)->v.set(k)}
        i18n?.forEach{(k, v)->v.set(k)}
    }
    override fun toJSON():String = "{" + """
      ${ruleset?.let{",\"ruleset\":${it._toJSON()}"}}
      ${query?.let{",\"query\":${it._toJSON()}"}}
      ${db?.let{",\"db\":${it._toJSON()}"}}
      ${style?.let{",\"style\":${it._toJSON()}"}}
      ${api?.let{",\"api\":${it._toJSON()}"}}
      ${i18n?.let{",\"i18n\":${it._toJSON()}"}}
    """.substring(1) + "}"
}