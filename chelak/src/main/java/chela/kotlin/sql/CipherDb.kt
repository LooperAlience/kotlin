package chela.kotlin.sql

import android.annotation.SuppressLint
import android.util.Log
import chela.kotlin.android.ChApp
import chela.kotlin.android.ChAsset
import chela.kotlin.core.ChCrypto
import chela.kotlin.model.Model
import net.sqlcipher.Cursor
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream

class CipherDb internal constructor(private val k:String, create:String = "", assetPath:String? = null, pw:String? = null){
    var msg = ""
    private val database:SQLiteDatabase
    init{
        val p = pw ?: ChCrypto.permanentPw()
        val root = "/data/data/${ChApp.packName}/databases/"
        val db = File(root + k)
        val isCreate = if(db.exists()) false
        else{
            File(root).mkdirs()
            assetPath?.let{
                if(db.createNewFile()) FileOutputStream(db).use{
                    it.write(ChAsset.bytes(assetPath))
                    it.close()
                }
            }
            true
        }
        Log.i("ch", "$db, $p")

        database = SQLiteDatabase.openOrCreateDatabase(db, p, null)
        if(isCreate && create.isNotBlank()) create.split(",").forEach{exec(it.trim())}
    }
    fun remove(){
        database.close()
        ChApp.app.deleteDatabase(k)
    }
    fun exec(k: String, vararg arg:Pair<String, Any>):Int{
        runQuery(k, *arg)
        val c = database.rawQuery("SELECT changes()", null)
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
    fun <T: Model> select(k:String, vararg arg:Pair<String, Any>, block:()->T):List<T>
        = runQuery(k, *arg)?.let {c->
            c.moveToFirst()
            val r = (0 until c.count).map {
                val v = block()
                c.columnNames.forEachIndexed { i, s ->
                    v[s] = when (c.getType(i)) {
                        Cursor.FIELD_TYPE_INTEGER -> c.getInt(i)
                        Cursor.FIELD_TYPE_FLOAT -> c.getFloat(i)
                        Cursor.FIELD_TYPE_STRING -> c.getString(i)
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
        val c = database.rawQuery("select last_insert_rowid()", null)
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
        if(it.query.size > 1) database.beginTransaction()
        it.query.forEachIndexed { i, query ->
            val a = arg[i]
            val q = query.substring(1)
            try {
                when (query[0]) {
                    'r' ->  c = database.rawQuery(q, if (a.isEmpty()) null else a) ?: run{
                        msg = "no result - $key"
                        null
                    }
                    'w'->if(a.isEmpty()) database.execSQL(q) else database.execSQL(q, a)
                }
            }catch(e:Throwable){msg = "error - $e"}
        }
        if(it.query.size > 1) database.endTransaction()
        return c
    }
}