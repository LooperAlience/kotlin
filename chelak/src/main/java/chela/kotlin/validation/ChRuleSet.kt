package chela.kotlin.validation

import chela.kotlin.validation.ChValidator.Companion.dmsg
import chela.kotlin.validation.rule.BaseRules
import chela.kotlin.validation.rule.RegRules
import chela.kotlin.validation.rule.TypeRules

class ChRuleSet(rule:String, private val msg:msg = dmsg){
    companion object{
        @JvmStatic private val baseRules = BaseRules()
        @JvmStatic private val typeRules = TypeRules()
        @JvmStatic private val regRules = RegRules()
        @JvmStatic private val _defined = mutableMapOf<String, ChRuleSet>()
        @JvmStatic private val regArg = """\[(.+)\]""".toRegex()
        @JvmStatic internal val emptyArg = listOf<String>()
        @JvmStatic fun isOk(v:Any):Boolean = v !is ChRuleSet
        @JvmStatic operator fun get(k:String):ChRuleSet = _defined[k] ?: run{
            if(ChRule.rules[k] == null) throw Exception("invalid rule:$k")
            val r = ChRuleSet(k)
            _defined[k] = r
            r
        }
    }
    var result:Any = false
    private val rules = rule.split("-or-").map{
        it.split("|").filter{it.isNotBlank()}.map ch@{v->
            val arg = regArg.find(v)?.let{it.groupValues[1].split(",").map{it.trim()}} ?: emptyArg
            var k = regArg.replace(v, "").trim().toLowerCase()
            ChRule.rules[k]?.let { (f, t)->{ it:Any->f.call(t, it, arg)!!}} ?: throw Exception("invalid key:$k")
        }
    }
    fun check(v: Any):Any{
        result = v
        return if(rules.any{
            it.all{
                result = it(result)
                result !is ChRule
            }
        }) result else this
    }
}