package chela.kotlin.resource

import chela.kotlin.core.*
import chela.kotlin.sql.ChSql
import org.json.JSONObject

object ChRes{
    @JvmStatic fun load(res:JSONObject, isInited:Boolean = false){
        res._forObject{k, obj->
            load(
                Res(k, obj),
                isInited
            )
        }
        res._array("remove")?._for<String>{ _, v->
            ChSql.DB?.s("ch_getId", "id" to v)?.let {
                if(it.isNotBlank()){
                    _try{JSONObject(it)}?.let{Res("", it).remove()}
                }
            }
            ChSql.DB?.exec("ch_remove", "id" to v)
        }
    }
    @JvmStatic fun load(res:Res, isInited:Boolean = false){
        if(
            if(isInited){
                res.setDb()
                ChSql.DB?.i("ch_exist")?.let{it > 0} ?: false
            }else ChSql.DB?.i("ch_id", "id" to res.id)?.let{it == 0} ?: false
        ){
            ChSql.DB?.exec("ch_add", "id" to res.id, "contents" to res.toJSON())
            if(!isInited) res.setDb()
            res.setRes()
        }
        if(isInited){
            ChSql.DB?.select("ch_get")?.forEach{ _, arr ->
                val v = arr.map{"$it"}
                _try{JSONObject(v[1])}?.let{
                    val r = Res(v[0], it)
                    r.setDb()
                    r.setRes()
                }
            }
        }
    }
    @JvmStatic fun base():Pair<String, String>{
        val c = mutableListOf<String>()
        val u = mutableListOf<String>()
        listOf("""
            CREATE TABLE IF NOT EXISTS ch_res(
                res_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
                id VARCHAR(255) NOT null,
                contents TEXT NOT null,
                UNIQUE(id)
            )
            --
            drop table ch_res
        """).forEachIndexed{ i, it ->
            val q = it.split("--")
            ChSql.addQuery("chc$i", q[0])
            ChSql.addQuery("chu$i", q[1])
            c += "chc$i"
            u += "chu$i"
        }
        return c.joinToString(",") to u.joinToString(",")
    }
    @JvmStatic fun baseQuery(){
        ChSql.addQuery("ch_exist", "select count(*)from ch_res")
        ChSql.addQuery("ch_add", "insert into ch_res(id,contents)values(@id:string@,@contents:string@)")
        ChSql.addQuery("ch_id", "select count(*)from ch_res where id=@id:string@")
        ChSql.addQuery("ch_remove", "delete from ch_res where id=@id:string@")
        ChSql.addQuery("ch_getId", "select contents from ch_res where id=@id:string@")
        ChSql.addQuery("ch_get", "select id, contents from ch_res")
    }
}