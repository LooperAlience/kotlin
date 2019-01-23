package chela.kotlin.resource

import android.util.Log
import chela.kotlin.core._array
import chela.kotlin.core._for
import chela.kotlin.core._forObject
import chela.kotlin.core._try
import chela.kotlin.sql.ChSql
import chela.kotlin.sql.CipherDb
import chela.kotlin.sql.DataBase
import org.json.JSONObject

object ChRes{
    @JvmStatic var inited = false
    @JvmStatic lateinit var db:DataBase
    @JvmStatic fun load(res:JSONObject){
        res._forObject{k, obj->load(Res(k, obj))}
        res._array("remove")?._for<String>{ _, v->
            _try{Res("", JSONObject(db.s("ch_getId", "id" to v))).remove()}
            db.exec("ch_remove", "id" to v)
        }
    }
    @JvmStatic private fun load(res:Res){
        if(db.i("ch_id", "id" to res.id) == 0){
            db.exec("ch_add", "id" to res.id, "contents" to res.toJSON())
            res.set()
        }
    }
    @JvmStatic fun init(){
        """
        ch_create--CREATE TABLE IF NOT EXISTS ch_res(
            res_rowid INTEGER PRIMARY KEY AUTOINCREMENT,
            id VARCHAR(255) NOT null,
            contents TEXT NOT null,
            UNIQUE(id)
        );
        ch_add--insert into ch_res(id,contents)values(@id:string@,@contents:string@);
        ch_id--select count(*)from ch_res where id=@id:string@;
        ch_remove--delete from ch_res where id=@id:string@;
        ch_getId--select contents from ch_res where id=@id:string@;
        ch_get--select id,contents from ch_res;
        ch_list--select id from ch_res
        """.split(";").forEach {
            val a = it.split("--")
            ChSql.addQuery(a[0].trim(), a[1].trim())
        }
        ChSql.addDb("ch", "ch_create", null, null)
        ChSql.db("ch"){
            db = it
            db.select("ch_get")?.forEach { _, arr ->
                val v = arr.map { "$it" }
                _try { JSONObject(v[1]) }?.let { Res(v[0], it).set() }
            }
        }
        inited = true
    }
    @JvmStatic val ids:String get()= db.select("ch_list")?.rs?.joinToString(",") {"${it[0]}"} ?: ""
}