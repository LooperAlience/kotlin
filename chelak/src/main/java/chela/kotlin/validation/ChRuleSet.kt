package chela.kotlin.validation

import android.util.Log
import chela.kotlin.model.Model
import chela.kotlin.regex.reParam
import chela.kotlin.sql.ChBaseDB
import chela.kotlin.validation.rule.BaseRules
import chela.kotlin.validation.rule.RegRules
import chela.kotlin.validation.rule.TypeRules

class ChRuleSet(rule:String){
    companion object{
        @JvmStatic private val _defined = mutableMapOf<String, ChRuleSet>()
        @JvmStatic private val baseRule = listOf(BaseRules(), TypeRules(), RegRules())
        @JvmStatic val string get() = ChRuleSet["string"]!!
        @JvmStatic fun set(k:String, rule:String, isWriteDB:Boolean = true){
            if(k.isBlank()) return
            ChBaseDB.ruleset.get()
            val key = k.trim().toLowerCase()
            if (_defined[key] != null) throw Exception("exist ruleset:$key")
            _defined[key] = ChRuleSet(rule)
            if(isWriteDB) ChBaseDB.ruleset.add(key, rule)
        }
        @JvmStatic operator fun get(k:String):ChRuleSet?{
            ChBaseDB.ruleset.get()
            return _defined[k.toLowerCase()]
        }
        @JvmStatic fun fromJson(k:String, json:String){
            JsonRuleset.jsonKey = k.trim()
            JsonRuleset.fromJson(json)
        }
        object JsonRuleset:Model(){
            @JvmStatic internal var jsonKey = ""
            override fun set(k: String, v:Any):Boolean{
                if(v is String) ChRuleSet.set("$jsonKey.$k", v)
                return true
            }
        }
        @JvmStatic fun isOk(k:String, v:Any) = (get(k) ?: string).check(v)
        @JvmStatic fun isOk(vararg kv:Pair<String, Any>) = kv.all {(k, v)->isOk(k, v) !is ChRuleSet}
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