package chela.kotlin.sql

import chela.kotlin.regex.reQueryParam
import chela.kotlin.regex.reTrimLine
import chela.kotlin.validation.ChRuleSet


class ChQuery(key:String, body: String){
    private class Item(val i:Int, val k:String, val ruleSet: ChRuleSet)
    private val items = mutableListOf<Map<String, Item>>()
    internal var msg = ""
    internal val query = run {
        reTrimLine.trim(body).split(";").map{
            var query = it.trim()
            query = if(query.toLowerCase().startsWith("select")) "r$query" else "w$query"
            val map = mutableMapOf<String, Item>()
            items += map
            reQueryParam.setItem(query){k, v->
                map[k] = Item(map.size, k, ChRuleSet[v] ?: run {
                    val rk = "$key.$k"
                    ChRuleSet.add(rk, v)
                    ChRuleSet[rk]!!
                })
            }
        }
    }
    internal fun param(param:Array<out Pair<String, Any>>):List<Array<String>>? = items.map{_param(it, param) ?: return null}
    private fun _param(item:Map<String, Item>, param:Array<out Pair<String, Any>>):Array<String>?{
        val r = MutableList(item.size){""}
        var cnt = 0
        param.forEach {(k, v)->
            item[k]?.let {
                val c = it.ruleSet.check(v)
                if(c !is ChRuleSet){
                    r[it.i] = c.toString()
                    cnt++
                }else{
                    msg = "invalid type:$k - ${it.ruleSet} - $c"
                    return null
                }
            }
        }
        return if(cnt != item.size){
            msg = "param not match:$cnt != ${item.size} - $query"
            null
        }else r.toTypedArray()
    }
}