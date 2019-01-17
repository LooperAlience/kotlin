package chela.kotlin.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import chela.kotlin.android.ChApp
import chela.kotlin.model.Model

class DataBase internal constructor(ctx: Context, db:String, ver:Int, val create:String = "", val upgrade:String = ""): SQLiteOpenHelper(ctx, db, null, ver){
    var msg = ""
    private val writer = writableDatabase
    private val reader = readableDatabase
    override fun onCreate(db: SQLiteDatabase?){
        if(create.isNotBlank()) create.split(",").forEach{exec(it.trim())}
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion:Int, newVersion:Int){
        if(upgrade.isNotBlank()) upgrade.split(",").forEach{exec(it.trim())}
    }
    fun remove(){
        close()
        ChApp.app.deleteDatabase(databaseName)
    }
    fun exec(k: String, vararg arg:Pair<String, Any>):Int{
        runQuery(k, *arg)
        val c = reader.rawQuery("SELECT changes()", null)
        val r = if (c != null && c.count > 0 && c.moveToFirst()) c.getInt(0) else 0
        c.close()
        return r
    }
    fun select(k:String, isRecord:Boolean = false, vararg arg:Pair<String, Any>): ChCursor?
        = runQuery(k, *arg)?.let{
            if(it.count > 0 && it.moveToFirst()) {
                val offCursor = ChCursor(it, isRecord)
                it.close()
                offCursor
            }else null
        }
    fun <T: Model> select(k:String, vararg arg:Pair<String, Any>, block:()->T):List<T>{
        val c = runQuery(k, *arg) ?: throw Exception("invalid getQuery result:$k")
        c.moveToFirst()
        val r = (0 until c.count).map {
            val v = block()
            c.columnNames.forEachIndexed { i, s ->
                v[s] = when(c.getType(i)){
                    Cursor.FIELD_TYPE_INTEGER ->c.getInt(i)
                    Cursor.FIELD_TYPE_FLOAT ->c.getFloat(i)
                    Cursor.FIELD_TYPE_STRING ->c.getString(i)
                    Cursor.FIELD_TYPE_BLOB ->c.getBlob(i)
                    else->c.getString(i)
                }
            }
            c.moveToNext()
            v
        }
        c.close()
        return r
    }
    fun lastId():Int{
        val c = reader.rawQuery("select last_insert_rowid()", null)
        val r = if(c.count > 0 && c.moveToFirst()) c.getInt(0) else -1
        c.close()
        return r
    }
    fun i(k: String, vararg arg:Pair<String, Any>): Int {
        val c = runQuery(k, *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getInt(0) else -1
        c.close()
        return r
    }
    fun f(k: String, vararg arg: Pair<String, Any>): Float {
        val c = runQuery(k, *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getFloat(0) else -1F
        c.close()
        return r
    }
    fun s(k: String, vararg arg: Pair<String, Any>): String {
        val c = runQuery(k, *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getString(0) else ""
        c.close()
        return r
    }

    /**
     * @param key
     * @param db
     * @param param With param, check whether getQuery values follow the {@link ChRuleSet}.
     */
    @SuppressLint("Recycle")
    private fun runQuery(key:String, vararg param:Pair<String, Any>):Cursor?{
        val it = ChSql.getQuery(key) ?: run {
            msg = "invalid query:$key"
            return null
        }
        val arg = it.param(param) ?: run{
            msg = it.msg
            return null
        }
        var c:Cursor? = null
        it.query.forEachIndexed { i, query ->
            val a = arg[i]
            val q = query.substring(1)
            try {
                when (query[0]) {
                    'r' ->  c = reader.rawQuery(q, if (a.isEmpty()) null else a) ?: run{
                        msg = "no result - $key"
                        null
                    }
                    'w'->if(a.isEmpty()) writer.execSQL(q) else writer.execSQL(q, a)
                }
            }catch(e:Throwable){msg = "error - $e"}
        }
        return c
    }
}