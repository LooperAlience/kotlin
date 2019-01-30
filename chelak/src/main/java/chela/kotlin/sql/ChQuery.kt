package chela.kotlin.sql

import chela.kotlin.regex.reQueryParam
import chela.kotlin.regex.reTrimLine
import chela.kotlin.validation.ChRuleSet


class ChQuery(key:String, body:String){
    private class Item(val i:Int, val k:String, val ruleSet: ChRuleSet)
    private val items = mutableListOf<Map<String, Item>>()
    private val replacer = mutableListOf<Map<String, String>>()
    internal var msg = ""
    internal val query = run {
        reTrimLine.trim(body).split(";").map{
            var query = it.trim()
            query = if(query.toLowerCase().startsWith("select")) "r$query" else "w$query"
            val map = mutableMapOf<String, Item>()
            val rep = mutableMapOf<String, String>()
            items += map
            replacer += rep
            reQueryParam.setItem(query){k, v->
                if(v.isBlank()) rep[k] = k
                else map[k] = Item(map.size, k, ChRuleSet[v] ?: run {
                    val rk = "$key.$k"
                    ChRuleSet.add(rk, v)
                    ChRuleSet[rk]!!
                })
            }
        }
    }
    internal fun param(param:Array<out Pair<String, Any>>):List<Pair<String, Array<String>>>? = items.mapIndexed{i, item->_param(query[i], item, replacer[i], param) ?: return null}
    private fun _param(q:String, item:Map<String, Item>, rep:Map<String, String>, param:Array<out Pair<String, Any>>):Pair<String, Array<String>>?{
        val r = MutableList(item.size){""}
        var iCnt = 0
        var rCnt = 0
        var query = q
        param.forEach {(k, v)->
            rep[k]?.let {
                query = query.replace(k, "$v")
                rCnt++
            }
            item[k]?.let {
                val c = it.ruleSet.check(v)
                if(c !is ChRuleSet){
                    r[it.i] = c.toString()
                    iCnt++
                }else{
                    msg = "invalid type:$k - ${it.ruleSet} - $c"
                    return null
                }
            }
        }
        return if(iCnt != item.size || rCnt != rep.size){
            msg = "param not match:item($iCnt, ${item.size}) replace($rCnt, ${rep.size}) - $query"
            null
        }else query to r.toTypedArray()
    }
}