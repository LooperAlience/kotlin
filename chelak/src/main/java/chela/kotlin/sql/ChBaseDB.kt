package chela.kotlin.sql

import android.util.Log
import chela.kotlin.i18n.ChI18n
import chela.kotlin.net.ChNet
import chela.kotlin.validation.ChRuleSet
import chela.kotlin.view.ChStyle

object ChBaseDB{
    fun base():Pair<String, String>{
        ChSql.addQuery("ch0", """
CREATE TABLE IF NOT EXISTS ch_id(
    id VARCHAR(255) NOT null,
    UNIQUE(id)
)""", false)
        ChSql.addQuery("ch1","""
CREATE TABLE IF NOT EXISTS ch_i18n(
    i18n_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT null,
    ver INTEGER NOT null,
    isOne VARCHAR(2) NOT null,
    UNIQUE(title)
)""", false)
        ChSql.addQuery("ch2", """
CREATE TABLE IF NOT EXISTS ch_i18nData(
    i18nData_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    i18n_rowid INTEGER REFERENCES ch_i18n(i18n_rowid) on delete restrict on update restrict,
    lang VARCHAR(2) NOT null,
    title VARCHAR(255) NOT null,
    contents TEXT NOT null,
    UNIQUE(i18n_rowid, lang, title)
)""", false)
        ChSql.addQuery("ch3", "CREATE INDEX ch_i18nData_idx0 ON ch_i18nData(title ASC,lang ASC)")
        ChSql.addQuery("ch4","""
CREATE TABLE IF NOT EXISTS ch_style(
    style_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT null,
    UNIQUE(title)
)""", false)
        ChSql.addQuery("ch5", """
CREATE TABLE IF NOT EXISTS ch_styleData(
    styleData_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    style_rowid INTEGER REFERENCES ch_style(style_rowid) on delete restrict on update restrict,
    title VARCHAR(255) NOT null,
    contents VARCHAR(255) NOT null,
    UNIQUE(style_rowid,title)
)""", false)
        ChSql.addQuery("ch6","""
CREATE TABLE IF NOT EXISTS ch_api(
    api_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT null,
    url VARCHAR(255) NOT null,
    method VARCHAR(10) NOT null,
    requestTask VARCHAR(255) NOT null,
    responseTask VARCHAR(255) NOT null,
    UNIQUE(title)
)""", false)
        ChSql.addQuery("ch7", """
CREATE TABLE IF NOT EXISTS ch_apiRequest(
    apiRequest_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    api_rowid INTEGER REFERENCES ch_api(api_rowid) on delete restrict on update restrict,
    title VARCHAR(255) NOT null,
    name VARCHAR(255) NOT null,
    rule VARCHAR(255) NOT null,
    task VARCHAR(255) NOT null,
    UNIQUE(api_rowid, title)
)""", false)
        ChSql.addQuery("ch8","""
CREATE TABLE IF NOT EXISTS ch_query(
    query_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT null,
    contents text NOT null,
    UNIQUE(title)
)""", false)
        ChSql.addQuery("ch9", """
CREATE TABLE IF NOT EXISTS ch_ruleset(
    ruleset_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT null,
    contents VARCHAR(255) NOT null,
    UNIQUE(title)
)""", false)
        ChSql.addQuery("d0", "drop table ch_id", false)
        ChSql.addQuery("d1", "drop table ch_i18n", false)
        ChSql.addQuery("d2", "drop table ch_i18nData", false)
        ChSql.addQuery("d3", "drop INDEX ch_i18nData_idx0", false)
        ChSql.addQuery("d4", "drop table ch_style", false)
        ChSql.addQuery("d5", "drop table ch_styleData", false)
        ChSql.addQuery("d6", "drop table ch_api", false)
        ChSql.addQuery("d7", "drop table ch_apiRequest", false)
        ChSql.addQuery("d8", "drop table ch_query", false)
        ChSql.addQuery("d9", "drop table ch_ruleset", false)
        return "ch0,ch1,ch2,ch3,ch4,ch5,ch6,ch7,ch8,ch9" to "d0,d1,d2,d3,d4,d5,d6,d7"
    }
    fun baseQuery(){
        id()
        sql()
        style()
        ruleset()
        i18n()
    }
    object id{
        @JvmStatic val ID = "id"
        operator fun invoke(){
            ChSql.addQuery("ch_id_exist", "select count(*) from ch_id where id=@id:string@", false)
            ChSql.addQuery("ch_id_add", "insert into ch_id(id)values(@id:string@)", false)
        }
        fun isExist(id:String):Boolean{
            Log.i("ch", "DB"+ChSql.DB())
            val r = ChSql.DB()?.i("ch_id_exist", "id" to id) == 1
            if(!r) ChSql.DB()?.exec("ch_id_add", "id" to id)
            return r
        }
    }
    object sql{
        operator fun invoke(){
            ChSql.addQuery("ch_query_get", "select title, contents from ch_query", false)
            ChSql.addQuery("ch_query_add", "REPLACE into ch_query(title, contents)values(@title:string@,@contents:string@)",false)
            ChSql.DB()?.select("ch_query_get", false)?.forEach { _, arr ->
                ChSql.addQuery("${arr[0]}", "${arr[1]}", false)
            }
        }
        fun add(key:String, body:String){
            ChSql.DB()?.exec("ch_query_add", "title" to key, "contents" to body.trim())
        }
    }
    object api{
        @JvmStatic val URL = "url"
        @JvmStatic val METHOD = "method"
        @JvmStatic val REQUESTTASK = "requestTask"
        @JvmStatic val RESPONSETASK = "responseTask"
        @JvmStatic val REQUEST = "request"
        @JvmStatic val REQUEST_NAME = "name"
        @JvmStatic val REQUEST_RULES = "rules"
        @JvmStatic val REQUEST_TASK = "task"
        @JvmStatic private var id = 0
        @JvmStatic private var isLoaded = false
        operator fun invoke(){
            ChSql.addQuery("ch_api_add", """
                REPLACE into ch_api(title,url,method,requestTask,responseTask)values
                (@title:string@,@url:string@,@method:string@,@requestTask:string@,@responseTask:string@)""",false)
            ChSql.addQuery("ch_apiRequster_add", """
                REPLACE into ch_apiRequest(api_rowid,title,name,rule,task)values
                (@id:int@,@title:string@,@name:string@,@rule:string@,@task:string@)""", false)
            ChSql.addQuery("ch_api_get", """
                select
                a.title,a.url,a.method,a.requestTask,a.responseTask,
                r.title,r.name,r.rule,r.task
                from ch_apiRequest r inner join ch_api a on r.api_rowid = a.api_rowid
                order by a.title
            """, false)
        }
        fun addApi(k:String, url:String, method:String, requestTask:String, responseTask:String){
            ChSql.DB()?.exec("ch_api_add", "title" to k, "url" to url, "method" to method, "requestTask" to requestTask, "responseTask" to responseTask)
            id = ChSql.DB()?.lastId() ?: 0
        }
        fun addItem(k:String, name:String, rule:String, task:String){
            if(id == 0) return
            ChSql.DB()?.exec("ch_apiRequster_add", "id" to id, "title" to k, "name" to name, "rule" to rule, "task" to task)
        }
        fun get(){
            if(isLoaded) return
            isLoaded = true
            var prev = ""
            var key = ""
            var url = ""
            var method = ""
            var reqTask = ""
            var resTask = ""
            var m = mutableMapOf<String, List<String>>()
            ChSql.DB()?.select("ch_api_get", false)?.forEach{ _, arr->
                val a = arr.map { "$it" }
                val k = a[0]
                if(k != prev){
                    if(prev.isNotBlank()) ChNet.setApi(key, url, method, reqTask, resTask, m, false)
                    prev = k
                    key = k
                    url = a[1]
                    method = a[2]
                    reqTask = a[3]
                    resTask = a[4]
                    m = mutableMapOf()
                }
                m[a[5]] = listOf(a[6],a[7],a[8])
            }
            if(prev.isNotBlank()) ChNet.setApi(key, url, method, reqTask, resTask, m, false)
        }
    }
    object i18n{
        @JvmStatic val VER = "ver"
        @JvmStatic val ISONE = "isOne"
        @JvmStatic val DATA = "data"

        private var id = 0
        private var isLoaded = false


        operator fun invoke(){
            ChSql.addQuery("ch_i18n_add", """
                REPLACE into ch_i18n(title,ver,isOne)values
                (@title:string@,@ver:int@,@isOne:string@)""",false)
            ChSql.addQuery("ch_i18nData_add", """
                REPLACE into ch_i18nData(i18n_rowid,lang,title,contents)values
                (@id:int@,@lang:string@,@title:string@,@contents:string@)""",false)
            ChSql.addQuery("ch_i18n_get", """
                select
                i.title,i.ver,i.isOne,
                d.lang,d.title,d.contents
                from ch_i18nData d inner join ch_i18n i on d.i18n_rowid = i.i18n_rowid
                order by i.title
            """, false)
        }
        fun addKey(k:String, ver:Int, isOne:String){
            ChSql.DB()?.exec("ch_i18n_add", "title" to k, "ver" to ver, "isOne" to isOne)
            id = ChSql.DB()?.lastId() ?: 0
        }
        fun addData(lang:String, k:String, v:String){
            if(id == 0) return
            ChSql.DB()?.exec("ch_i18nData_add", "id" to id, "lang" to lang, "title" to k, "contents" to v)
        }
        fun get(){
            if(isLoaded) return
            isLoaded = true
            var prev = ""
            var prevLang = ""
            var key = ""
            var ver = 0
            var isOne = ""
            var lang = ""
            var m = mutableMapOf<String,Map<String, String>>()
            var lm = mutableMapOf<String,String>()
            ChSql.DB()?.select("ch_i18n_get", false)?.forEach{ _, arr->
                val k = "${arr[0]}"
                if(k != prev){
                    if(prev.isNotBlank()) ChI18n.set(key, ver, isOne, m, false)
                    prev = k
                    key = k
                    ver = "${arr[1]}".toInt()
                    isOne = "${arr[2]}"
                    prevLang = ""
                    m = mutableMapOf()
                }
                val l = "${arr[3]}"
                if(l != prevLang){
                    prevLang = l
                    lang = l
                    lm = mutableMapOf()
                    m[lang] = lm
                }
                lm["${arr[4]}"] = "${arr[5]}"
            }
            if(prev.isNotBlank()) ChI18n.set(key, ver, isOne, m, false)
        }
    }
    object style{
        private var id = 0
        private var isLoaded = false
        operator fun invoke(){
            ChSql.addQuery("ch_style_add", "insert into ch_style(title)values(@title:string@)",false)
            ChSql.addQuery("ch_styleData_add", "REPLACE into ch_styleData(style_rowid,title,contents)values(@styleid:int@, @title:string@, @contents:string@)",false)
            ChSql.addQuery("ch_style_get", """
                select s.title,d.title,d.contents
                from ch_styleData d inner join ch_style s on d.style_rowid = s.style_rowid
                order by s.title
            """, false)
        }
        fun addStyle(k:String){
            ChSql.DB()?.exec("ch_style_add", "title" to k)
            id = ChSql.DB()?.lastId() ?: 0
        }
        fun addData(k:String, v:String){
            if(id == 0) return
            ChSql.DB()?.exec("ch_styleData_add", "styleid" to id, "title" to k, "contents" to v)
        }
        fun get(){
            if(isLoaded) return
            isLoaded = true
            var prev = ""
            var m = mutableMapOf<String, Any>()
            ChSql.DB()?.select("ch_style_get", false)?.forEach{ _, arr->
                val style = "${arr[0]}"
                if(style != prev){
                    m = mutableMapOf()
                    ChStyle.items[style] = m
                    prev = style
                }
                val data = "${arr[2]}"
                val v = data.substring(1)
                m["${arr[1]}"] = when(data[0]){
                    'i'->v.toInt()
                    'f'->v.toFloat()
                    'l'->v.toLong()
                    'd'->v.toDouble()
                    'b'->v.toBoolean()
                    else->v
                }
            }
        }
    }
    object ruleset{
        private var isLoaded = false
        operator fun invoke(){
            ChSql.addQuery("ch_ruleset_get", "select title,contents from ch_ruleset", false)
            ChSql.addQuery("ch_ruleset_add", "REPLACE into ch_ruleset(title, contents)values(@title:string@,@contents:string@)",false)
        }
        fun add(key:String, body:String){
            ChSql.DB()?.exec("ch_ruleset_add", "title" to key, "contents" to body.trim())
        }
        fun get(){
            if(isLoaded) return
            isLoaded = true
            ChSql.DB()?.select("ch_ruleset_get", false)?.forEach { _, arr ->
                ChRuleSet.set("${arr[0]}", "${arr[1]}", false)
            }
        }
    }
}