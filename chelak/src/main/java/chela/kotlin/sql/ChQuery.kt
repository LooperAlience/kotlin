package chela.kotlin.sql

import chela.kotlin.validation.ChRuleSet

private val regParam =  "@(?:([^@:]+)(?::([^@:]+))?)@".toRegex()
private val regTrim =  """[\n\r]""".toRegex()
internal class Item(val i:Int, val k:String, val ruleSet: ChRuleSet)
class ChQuery(key:String, body: String){
    internal val items = mutableMapOf<String, Item>()
    internal val query = regTrim.replace(regParam.replace(body){
        val k = it.groupValues[1]
        val v = it.groupValues[2]

        items[k] = Item(items.size, k, ChRuleSet[v] ?: run{
            ChRuleSet.add("$key.k", v)
            ChRuleSet["$key.k"]!!
        })
         "?"
    }, " ").trim()
    internal fun param(param:Array<out Pair<String, Any>>):Array<String>{
        val r = MutableList(items.size){""}
        var cnt = 0
        param.forEach {(k, v)->
            items[k]?.let {
                val c = it.ruleSet.check(v)
                if(c !is ChRuleSet){
                    r[it.i] = c.toString()
                    cnt++
                }else throw Exception("invalid type:$k - ${it.ruleSet} - $c")
            }
        }
        if(cnt != items.size) throw Exception("param not match:$cnt != ${items.size} - $query")
        return r.toTypedArray()
    }
}