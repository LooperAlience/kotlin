package chela.kotlin.resource

import android.util.Log
import chela.kotlin.core.*
import chela.kotlin.net.ChNet
import chela.kotlin.sql.ChSql
import org.json.JSONObject

object ChRes{
    @JvmStatic fun load(res:JSONObject, isInited:Boolean = false){
        res._string("base")?.let{ChNet.apiBaseURL(it)}
        res._forObject{k, obj->
            load(
                Res(k, obj),
                isInited
            )
        }
        res._array("remove")?._for<String>{ _, v->
            ChSql.DB?.let{
                _try{Res("", JSONObject(it.s("ch_getId", "id" to v))).remove()}
                it.exec("ch_remove", "id" to v)
            }
        }
    }
    @JvmStatic fun load(res:Res, isInited:Boolean = false){
        res.setDb()
        ChSql.DB?.let{
            val isSave = if(isInited) it.i("ch_exist") else it.i("ch_id", "id" to res.id)
            if(isSave == 0) it.exec("ch_add", "id" to res.id, "contents" to res.toJSON())
            if(isInited) it.select("ch_get")?.forEach{_, arr->
                val v = arr.map{"$it"}
                _try{
                    val r = Res(v[0], JSONObject(v[1]))
                    r.setDb()
                    r.setRes()
                }
            }
        }
        res.setRes()
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
    @JvmStatic fun baseQuery() = """
        ch_exist--select count(*)from ch_res
        ch_add--insert into ch_res(id,contents)values(@id:string@,@contents:string@)
        ch_id--select count(*)from ch_res where id=@id:string@
        ch_remove--delete from ch_res where id=@id:string@
        ch_getId--select contents from ch_res where id=@id:string@
        ch_get--select id,contents from ch_res
        ch_list--select id from ch_res
    """.trim().split("\n").forEach {
        val a = it.split("--")
        ChSql.addQuery(a[0].trim(), a[1].trim())
    }
}