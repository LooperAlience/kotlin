package chela.kotlin.resource

import chela.kotlin.core._boolean
import chela.kotlin.core._int
import chela.kotlin.core._string
import chela.kotlin.core.toJSON
import chela.kotlin.sql.ChSql
import org.json.JSONObject

class Db(v:JSONObject){
    private var create = v._string("create") ?: ""
    private var pass = v._string("pass")
    private var asset = v._string("asset")
    fun set(k:String) = ChSql.addDb(k, create, asset, pass)
    fun remove(k:String) = ChSql.removeDb(k)
}