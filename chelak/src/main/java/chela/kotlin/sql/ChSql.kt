package chela.kotlin.sql

typealias B = kotlin.Boolean
internal enum class Type{
    String{override fun IS(v:Any): B = v is kotlin.String},
    Boolean{override fun IS(v:Any): B = v is B },
    Int{override fun IS(v:Any): B = v is kotlin.Int},
    Long{override fun IS(v:Any): B = v is kotlin.Long},
    Float{override fun IS(v:Any): B = v is kotlin.Float},
    Double{override fun IS(v:Any): B = v is kotlin.Double};
    abstract fun IS(v:Any): B
}
internal data class Item(val i:Int, val k:String, val t: Type)
private val regParam =  "@([^@]+)@".toRegex()
private val types = Type.values().map { it.name to it }.toMap()
private val regComment =  "\\/\\*.*\\*\\/".toRegex()
open class ChSql{
    private val queries = mutableMapOf<String, ChQuery>()
    fun load(vararg files:String){
        for(f in files){
            for(str in f.replace(regComment, "").split("#")) {
                if (str.isBlank()) continue
                val i = str.indexOf("\n")
                query(str.substring(0, i).trim(), str.substring(i + 1).trim().replace("\n", " "))
            }
        }
    }
    fun query(key:String, body:String){
        if(key.isBlank()) return
        queries.put(key, ChQuery(body))
    }
    protected fun query(key:String, vararg param:Pair<String, Any>):Pair<String, Array<String>>{
        queries[key]?.let {
            return key to it.query(param)
        } ?: throw Exception("invalid query:$key")
    }
}
class ChQuery(body: String){
    private val items = mutableMapOf<String, Item>()
    private val query = regParam.replace(body) ch@{
        val v = it.groupValues[1]
        val i = v.indexOf(":")
        items[v] = Item(
            items.size,
            v.substring(0, i),
            types[v.substring(i + 1)] ?: Type.String
        )
        return@ch "?"
    }
    internal fun query(param:Array<out Pair<String, Any>>):Array<String>{
        val r = mutableListOf<String>()
        var cnt = 0
        param.forEach {(k, v)->
            items[k]?.let {
                if(it.t.IS(v)){
                    r.add(it.i, v.toString())
                    cnt++
                }else throw Exception("invalid type:$k - ${it.t} - $v")
            }
        }
        if(cnt != items.size) throw Exception("param not match:$cnt != ${items.size}")
        return r.toTypedArray()
    }
}