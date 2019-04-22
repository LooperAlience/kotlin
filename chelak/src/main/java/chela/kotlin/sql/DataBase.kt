package chela.kotlin.sql

import android.annotation.SuppressLint
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.model.Model
import net.sqlcipher.Cursor
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper

class DataBase internal constructor(private val db:String, ver:Int, private val create:String = "", private val upgrade:String = "", pw:String? = null):SQLiteOpenHelper(ChApp.app, db, null, ver){
    var msg = ""
    private val writer:SQLiteDatabase
    private val reader:SQLiteDatabase
    private var isCreate = false
    private var isUpgrade = false
    init{
        writer = getWritableDatabase(pw)
        reader = getReadableDatabase(pw)
        if(isCreate && create.isNotBlank()){
            create.split(",").forEach{exec(it.trim())}
        }
        else if(isUpgrade && upgrade.isNotBlank()) upgrade.split(",").forEach{exec(it.trim())}
    }
    override fun onCreate(db:net.sqlcipher.database.SQLiteDatabase?){isCreate = true}
    override fun onUpgrade(db:net.sqlcipher.database.SQLiteDatabase?, oldVersion: Int, newVersion: Int){isUpgrade = true}
    fun remove(){
        close()
        ChApp.app.deleteDatabase(db)
    }
    fun exec(k: String, vararg arg:Pair<String, Any>):Int{
        runQuery(k, *arg)
        val c = reader.rawQuery("SELECT changes()", null)
        val r = if (c != null && c.count > 0 && c.moveToFirst()) c.getInt(0) else 0
        c.close()
        return r
    }
    fun select(k:String, isRecord:Boolean = false, vararg arg:Pair<String, Any>) = runQuery(k, *arg)?.let{
        val r = if(it.count > 0 && it.moveToFirst()) ChCursor(it, isRecord) else null
        it.close()
        r
    }
    fun <T:Model> select(k:String, vararg arg:Pair<String, Any>, block:()->T) = runQuery(k, *arg)?.let {c->
        c.moveToFirst()
        val r = (0 until c.count).map {
            val v = block()
            c.columnNames.forEachIndexed { i, s ->
                v[s] = when (c.getType(i)) {
                    Cursor.FIELD_TYPE_INTEGER -> c.getInt(i)
                    Cursor.FIELD_TYPE_FLOAT -> c.getFloat(i)
                    Cursor.FIELD_TYPE_BLOB -> c.getBlob(i)
                    else -> c.getString(i)
                }
            }
            c.moveToNext()
            v
        }
        c.close()
        r
    } ?: listOf()
    fun lastId():Int{
        val c = reader.rawQuery("select last_insert_rowid()", null)
        val r = if(c.count > 0 && c.moveToFirst()) c.getInt(0) else -1
        c.close()
        return r
    }
    fun i(k: String, vararg arg:Pair<String, Any>): Int =
        runQuery(k, *arg)?.let { c ->
            val r = if (c.count > 0 && c.moveToFirst()) c.getInt(0) else -1
            c.close()
            return r
        } ?: -1
    fun l(k: String, vararg arg:Pair<String, Any>): Long =
        runQuery(k, *arg)?.let { c ->
            val r = if (c.count > 0 && c.moveToFirst()) c.getLong(0) else -1L
            c.close()
            return r
        } ?: -1L
    fun d(k: String, vararg arg:Pair<String, Any>): Double =
        runQuery(k, *arg)?.let { c ->
            val r = if (c.count > 0 && c.moveToFirst()) c.getDouble(0) else -1.0
            c.close()
            return r
        } ?: -1.0
    fun f(k: String, vararg arg: Pair<String, Any>): Float =
        runQuery(k, *arg)?.let{c->
            val r = if(c.count > 0 && c.moveToFirst()) c.getFloat(0) else -1F
            c.close()
            return r
        } ?: -1F
    fun s(k: String, vararg arg: Pair<String, Any>): String =
        runQuery(k, *arg)?.let { c ->
            val r = if (c.count > 0 && c.moveToFirst()) c.getString(0) else ""
            c.close()
            return r
        } ?: ""
    /**
     * @param key
     * @param db
     * @param param With param, check whether getQuery values follow the {@link ChRuleSet}.
     */
    @SuppressLint("Recycle")
    private fun runQuery(key:String, vararg param:Pair<String, Any>): Cursor?{
        val it = ChSql.getQuery(key) ?: run {
            msg = "invalid query:$key"
            return null
        }
        val arg = it.param(param) ?: run{
            msg = it.msg
            return null
        }
        var c:Cursor? = null
        if(arg.size > 1) writer.beginTransaction()
        arg.forEach{(query, a)->
            val q = query.substring(1)
            if(Ch.debugLevel > 1){
                Log.i("ch", "query:$query")
                Log.i("ch", "arg:${a.joinToString(", ")}, ${a.isEmpty()}")
            }
            try {
                when (query[0]) {
                    'r' -> {
                        c?.let{it.close()}
                        c = reader.rawQuery(q, if (a.isEmpty()) null else a) ?: run {
                            msg = "no result - $key"
                            null
                        }
                    }
                    'w'->if(a.isEmpty()) writer.execSQL(q) else writer.execSQL(q, a)
                }
            }catch(e:Throwable){msg = "error - $e"}
        }
        if(it.query.size > 1){
            writer.setTransactionSuccessful()
            writer.endTransaction()
        }
        if(Ch.debugLevel > 1) Log.i("ch", "cursor:$c")
        return c
    }
}