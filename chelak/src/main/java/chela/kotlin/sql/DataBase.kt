package chela.kotlin.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import chela.kotlin.android.ChApp
import chela.kotlin.model.Model

class DataBase internal constructor(ctx: Context, db:String, ver:Int, val create:String = "", val upgrade:String = ""): SQLiteOpenHelper(ctx, db, null, ver){
    private val writer = writableDatabase
    private val reader = readableDatabase
    override fun onCreate(db: SQLiteDatabase?){
        if(create.isNotBlank()) create.split(",").forEach{exec(it)}
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion:Int, newVersion:Int){
        if(upgrade.isNotBlank()) upgrade.split(",").forEach{exec(it)}
        if(create.isNotBlank()) create.split(",").forEach{exec(it)}
    }
    fun remove(){
        close()
        ChApp.app.deleteDatabase(databaseName)
    }
    fun exec(k: String, vararg arg:Pair<String, Any>):Int{
        getQuery(k, 'w', *arg)
        val c = reader.rawQuery("SELECT changes()", null)
        val r = if(c != null && c.count > 0 && c.moveToFirst()) c.getInt(0) else 0
        c.close()
        return r
    }
    fun exec(k: String, model: Model):Int{
        val query = ChSql.query(k) ?: throw Exception("invalid query:$k")
        val arg = mutableListOf<Pair<String, Any>>()
        query.items.forEach { (k, v) -> arg += k to model[k] }
        getQuery(k, 'w', *arg.toTypedArray())
        val c = reader.rawQuery("SELECT changes()", null)
        val r = if(c != null && c.count > 0 && c.moveToFirst()) c.getInt(0) else 0
        c.close()
        return r
    }
    fun select(k:String, isRecord:Boolean = false, vararg arg:Pair<String, Any>): ChCursor?
        = getQuery(k, 'r', *arg)?.let{
            if(it.count > 0 && it.moveToFirst()) {
                val offCursor = ChCursor(it, isRecord)
                it.close()
                offCursor
            }else null
        }
    fun <T: Model> select(k:String, vararg arg:Pair<String, Any>, block:()->T):List<T>{
        val c = getQuery(k, 'r', *arg) ?: throw Exception("invalid query result:$k")
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
        val c = getQuery(k, 'r', *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getInt(0) else -1
        c.close()
        return r
    }
    fun f(k: String, vararg arg: Pair<String, Any>): Float {
        val c = getQuery(k, 'r', *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getFloat(0) else -1F
        c.close()
        return r
    }
    fun s(k: String, vararg arg: Pair<String, Any>): String {
        val c = getQuery(k, 'r', *arg)!!
        val r = if(c.count > 0 && c.moveToFirst()) c.getString(0) else ""
        c.close()
        return r
    }

    /**
     * @param key
     * @param db
     * @param param With param, check whether query values follow the {@link ChRuleSet}.
     */
    @SuppressLint("Recycle")
    private fun getQuery(key:String, db:Char, vararg param:Pair<String, Any>): Cursor?{
        val it = ChSql.query(key) ?: throw Exception("invalid query:$key")
        val arg = it.param(param)
        return if(db == 'r') reader.rawQuery(it.query, if(arg.isEmpty()) null else arg)
            else{
                if(arg.isEmpty()) writer.execSQL(it.query)
                else writer.execSQL(it.query, arg)
                null
            }
    }
}