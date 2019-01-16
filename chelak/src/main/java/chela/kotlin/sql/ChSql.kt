package chela.kotlin.sql

import chela.kotlin.Ch

object ChSql{
    @JvmStatic private val queries = mutableMapOf<String, ChQuery>()
    @JvmStatic private val Dbs = mutableMapOf<Any, DataBase>()
    @JvmStatic private var defaultDB = ""
    @JvmStatic val DB:DataBase? get() = Dbs[defaultDB]

    @JvmStatic fun db(id:Any):DataBase? = Dbs[id]
    @JvmStatic fun addDb(k:String, ver:Int, create: String, upgrade: String, isDefault:Boolean){
        if(Dbs[k] != null) throw Throwable("exist db:$k")
        if(!Ch.isInited()) throw Throwable("Ch is not inited!")
        if(isDefault) defaultDB = k
        Dbs[k] = DataBase(Ch.app.app, k, ver, create, upgrade)
    }
    @JvmStatic fun removeDb(k:String) = Dbs[k]?.let{it.remove()}
    @JvmStatic fun query(key:String) = queries[key]
    @JvmStatic fun addQuery(k:String, body:String){if(k.isNotBlank()) queries[k] = ChQuery(k, body)}
    @JvmStatic fun removeQuery(k:String) = queries.remove(k)

    /*
    @JvmStatic private val regComment =  "\\/\\*.*\\*\\/".toRegex()
    @JvmStatic fun loadSql(files:List<String>) = files.forEach {loadSql(it)}
    @JvmStatic fun loadSql(it:String){
        var i = it.indexOf("\n")
        val id = it.substring(0, i).trim()
        it.substring(i + 1).trim().replace(regComment, "").split("#").forEach{
            if(it.isBlank()) return@forEach
            val t = it.trim()
            i = t.indexOf("\n")
            if(i == -1) return@forEach
            val k = t.substring(0, i).trim().toLowerCase()
            val v = t.substring(i + 1).trim().replace("\n", " ")
            i = k.indexOf("{")
            if(i == -1) addQuery(k, v) else ChRuleSet.fromJson(k.substring(0, i), k.substring(i) + v)
        }
    }
    */
}