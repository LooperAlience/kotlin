package chela.kotlin.sql

import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.android.ChAsset
import chela.kotlin.crypto.ChCrypto
import java.io.File
import java.io.FileOutputStream

typealias prov = (k:String, resolve:(v:String)->Unit)->Unit
typealias dbGet = (DataBase)->Unit
object ChSql{
    @JvmStatic private val queries = mutableMapOf<String, ChQuery>()
    @JvmStatic private val Dbs = mutableMapOf<String, DataBase>()
    @JvmStatic private val providers = mutableMapOf<String, prov>()
    @JvmStatic private val dbProvider = mutableMapOf<String, MutableList<dbGet>>()
    @JvmStatic fun addProvider(k:String, provider:prov) = providers.put(k, provider)
    @JvmStatic fun db(k:String) = Dbs[k] ?: throw Throwable("invalid db $k")
    @JvmStatic fun addDb(k:String, create:String, assetPath:String?, pass:String?){
        if(Dbs[k] != null) throw Throwable("exist db:$k")
        if(!Ch.isInited()) throw Throwable("Ch is not inited!")
        assetPath?.let {a->
            val root = "/" + "data/data/${ChApp.packName}/databases/"
            val db = File(root + k)
            if(!db.exists()) {
                File(root).mkdirs()
                if (db.createNewFile()) FileOutputStream(db).use {
                    it.write(ChAsset.bytes(a))
                    it.close()
                }
            }
        }
        run{Dbs[k] = DataBase(k, 1, create, "", pass ?: ChCrypto.permanentPw())}
    }
    @JvmStatic fun removeDb(k:String) = Dbs[k]?.let{it.remove()}

    @JvmStatic fun getQuery(key:String) = queries[key]
    @JvmStatic fun addQuery(k:String, body:String){if(k.isNotBlank()) queries[k] = ChQuery(k, body)}
    @JvmStatic fun removeQuery(k:String) = queries.remove(k)
}