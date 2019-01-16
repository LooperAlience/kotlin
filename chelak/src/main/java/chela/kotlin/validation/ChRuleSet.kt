package chela.kotlin.validation

import chela.kotlin.regex.reParam
import chela.kotlin.validation.rule.BaseRules
import chela.kotlin.validation.rule.RegRules
import chela.kotlin.validation.rule.TypeRules

class ChRuleSet(rule:String){
    companion object{
        @JvmStatic private val _defined = mutableMapOf<String, ChRuleSet>()
        @JvmStatic private val baseRule = listOf(BaseRules(), TypeRules(), RegRules())
        @JvmStatic val string get() = ChRuleSet["string"]!!
        @JvmStatic fun add(k:String, rule:String){
            if(k.isBlank() || rule.isBlank()) return
            val key = k.trim().toLowerCase()
            _defined[key] = ChRuleSet(rule)
        }
        @JvmStatic fun remove(k:String) = _defined.remove(k)
        @JvmStatic operator fun get(k:String):ChRuleSet? = _defined[k.toLowerCase()]
        @JvmStatic fun check(k:String, v:Any) = (get(k) ?: string).check(v)
        @JvmStatic fun isOk(vararg kv:Pair<String, Any>) = kv.all {(k, v)-> check(k, v) !is ChRuleSet}
    }
    private val rules = rule.split("-or-").map{
        it.split("|").filter{it.isNotBlank()}.map{v->
            val (k, arg) = reParam.parse(v)
            ChRule[k](arg)
        }
    }
    fun check(v: Any):Any{
        var r = v
        return if(rules.any{
            it.all{
                r = it(r)
                r !is ChRule
            }
        }) r else this
    }
}