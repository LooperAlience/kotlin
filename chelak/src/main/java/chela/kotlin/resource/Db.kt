package chela.kotlin.resource

import chela.kotlin.core._boolean
import chela.kotlin.core._int
import chela.kotlin.core._string
import chela.kotlin.core.toJSON
import chela.kotlin.sql.ChSql
import org.json.JSONObject

class Db(v:JSONObject){
    internal var isDefault:Boolean = false
    internal var ver:Int = 0
    internal var create:String = ""
    internal var upgrade:String = ""
    internal var asset:String = ""
    init{
        v._boolean("isDefault", "isdefault")?.let{isDefault = it}
        v._string("asset")?.let{asset = it}
        v._int("ver")?.let { ver = it }
        v._string("create")?.let { create = it }
        v._string("upgrade")?.let { upgrade = it }
    }
    fun remove(k:String) = ChSql.removeDb(k)
}