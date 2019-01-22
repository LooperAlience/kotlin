package chela.kotlin.sql

import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.android.ChAsset
import java.io.File
import java.io.FileOutputStream


object ChSql{
    @JvmStatic private val queries = mutableMapOf<String, ChQuery>()
    @JvmStatic private val Dbs = mutableMapOf<Any, DataBase>()
    @JvmStatic private var defaultDB = ""
    @JvmStatic val DB:DataBase? get() = Dbs[defaultDB]

    @JvmStatic fun db(id:Any):DataBase? = Dbs[id]
    @JvmStatic fun addDb(k:String, isDefault:Boolean, create:String, assetPath:String?, pwKey:String?){
        if(Dbs[k] != null) throw Throwable("exist db:$k")
        if(!Ch.isInited()) throw Throwable("Ch is not inited!")
        if(isDefault) defaultDB = k
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
        Dbs[k] = DataBase(k, 1, create, "", "helloworld1234") //pwKey
    }
    @JvmStatic fun removeDb(k:String) = Dbs[k]?.let{it.remove()}

    @JvmStatic fun getQuery(key:String) = queries[key]
    @JvmStatic fun addQuery(k:String, body:String){if(k.isNotBlank()) queries[k] = ChQuery(k, body)}
    @JvmStatic fun removeQuery(k:String) = queries.remove(k)
}