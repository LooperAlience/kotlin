package chela.kotlin.sql

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import chela.kotlin.Ch
import chela.kotlin.core.*
import chela.kotlin.model.Model
import chela.kotlin.validation.ChRuleSet
import org.json.JSONObject

object ChSql{
    interface Watcher{
        fun onInit(sql: Sql) {}
        fun onCreate(sql: Sql) {}
        fun onUpgrade(sql: Sql, oldVersion: Int, newVersion: Int) {}
    }

    @JvmStatic private val sql = mutableMapOf<Any, Sql>()
    @JvmStatic private var defaultDB = ""
    @JvmStatic val DB = get{sql[defaultDB]}

    @JvmStatic val queries = mutableMapOf<String, ChQuery>()
    @JvmStatic internal fun query(key:String) = queries[key]

    @JvmStatic fun load(v:JSONObject){
        v._forObject{k, db->
            val v = db._int("ver")
            val c = db._string("create")
            val u = db._string("update")
            _requiredNotNull(v, c, u){
                val ver = v!!
                var create = c!!
                var update = u!!
                db._list<String>("base")?.let{loadSql(it.map{Ch.asset.string(it)})}
                if(db._boolean("isDefault") == true){
                    defaultDB = k
                    ChBaseDB.base().let { (c, u)->
                        create = "$c,$create"
                        update = "$u,$update"
                    }
                }
                Ch.sql.addDb(k, k, ver, create, update)
            }
        }
        if(defaultDB != "") ChBaseDB.baseQuery()
    }
    @JvmStatic private val regComment =  "\\/\\*.*\\*\\/".toRegex()
    @JvmStatic fun loadSql(files:List<String>) = files.forEach {
        var i = it.indexOf("\n")
        val id = it.substring(0, i).trim()
        if(ChBaseDB.id.isExist(id)) return@forEach
        it.substring(i + 1).trim().replace(regComment, "").split("#").forEach ch@{
            if(it.isBlank()) return@ch
            val t = it.trim()
            i = t.indexOf("\n")
            if(i == -1) return@ch
            val k = t.substring(0, i).trim().toLowerCase()
            val v = t.substring(i + 1).trim().replace("\n", " ")
            i = k.indexOf("{")
            if(i == -1) addQuery(k, v) else ChRuleSet.fromJson(k.substring(0, i), k.substring(i) + v)
        }
    }
    @JvmStatic fun addQuery(k:String, body:String, isWriteDB:Boolean = true){
        if(k.isNotBlank()){
            queries.put(k, ChQuery(body))
            if(isWriteDB) ChBaseDB.sql.add(k, body)
        }
    }
    @JvmStatic operator fun get(id:Any):Sql? = sql[id]
    @JvmStatic fun addDb(id: Any, db: String, ver: Int, c: Watcher?): Sql {
        if (sql[id] == null) sql[id] = Sql(Ch.app.app, db, ver, c)
        return sql[id]!!
    }
    @JvmStatic fun addDb(id: Any, db: String, ver: Int, create: String = "", upgrade: String = ""): Sql {
        if (sql[id] == null) sql[id] = Sql(Ch.app.app, db, ver, create, upgrade)
        return sql[id]!!
    }
}
class Sql internal constructor(ctx:Context, db:String, ver:Int, c:ChSql.Watcher? = null): SQLiteOpenHelper(ctx, db, null, ver){
    internal constructor(ctx:Context, db:String, ver:Int, create:String = "", upgrade:String = ""):
            this(ctx, db, ver, object:ChSql.Watcher{
                override fun onCreate(sql: Sql){
                    if(create.isNotBlank()) create.split(",").forEach{sql.exec(it)}
                }
                override fun onUpgrade(sql: Sql, oldVersion: Int, newVersion: Int) {
                    if(upgrade.isNotBlank()) upgrade.split(",").forEach{sql.exec(it)}
                    onCreate(sql)
                }
            })
    private val writer = writableDatabase
    private val reader = readableDatabase
    private var isOnCreate = false
    private var isOnUpgrade = false
    private var oldV = 0
    private var newV = 0
    init{
        c?.let{
            c.onInit(this)
            if(isOnCreate) c.onCreate(this)
            else if(isOnUpgrade) c.onUpgrade(this, oldV, newV)
        }
    }
    override fun onCreate(db:SQLiteDatabase?){isOnCreate = true}
    override fun onUpgrade(db:SQLiteDatabase?, oldVersion:Int, newVersion:Int){
        isOnUpgrade = true
        oldV = oldVersion
        newV = newVersion
    }
    fun exec(k: String, vararg arg:Pair<String, Any>):Int{
        getQuery(k, 'w', *arg)
        val c = reader.rawQuery("SELECT changes()", null)
        val r = if(c != null && c.count > 0 && c.moveToFirst()) c.getInt(0) else 0
        c.close()
        return r
    }
    fun select(k:String, isRecord:Boolean = false, vararg arg:Pair<String, Any>):ChCursor?
        = getQuery(k, 'r', *arg)?.let{
            it.moveToFirst()
            val offCursor = ChCursor(it, isRecord)
            it.close()
            offCursor
        }
    fun <T: Model> select(k:String, vararg arg:Pair<String, Any>, block:()->T):List<T>{
        val c = getQuery(k, 'r', *arg) ?: throw Exception("invalid query result:$k")
        c.moveToFirst()
        val r = (0 until c.count).map {
            val v = block()
            c.columnNames.forEachIndexed { i, s ->
                v[s] = when(c.getType(i)){
                    Cursor.FIELD_TYPE_INTEGER->c.getInt(i)
                    Cursor.FIELD_TYPE_FLOAT->c.getFloat(i)
                    Cursor.FIELD_TYPE_STRING->c.getString(i)
                    Cursor.FIELD_TYPE_BLOB->c.getBlob(i)
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
    private fun getQuery(key:String, db:Char, vararg param:Pair<String, Any>):Cursor?{
        val it = ChSql.query(key) ?: throw Exception("invalid param:$key")
        val arg = it.param(param)
        return if(db == 'r') reader.rawQuery(it.query, if(arg.isEmpty()) null else arg)
            else{
                if(arg.isEmpty()) writer.execSQL(it.query)
                else writer.execSQL(it.query, arg)
                null
            }
    }
}