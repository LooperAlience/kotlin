package chela.kotlin.core

import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.sql.ChSql
import chela.kotlin.validation.ChRuleSet

object ChRules {
    @JvmStatic fun isOk(vararg kv:Pair<String,Any>):Boolean = kv.all {(k, v)->
        val r = when{
            k.isBlank() -> ChRuleSet["string"]
            !k.contains(".") -> ChRuleSet[k]
            else ->
                ChSql.rulesets[k] ?:
                Ch.vm.viewmodel(k.split(".")) as? ChRuleSet ?:
                ChRuleSet["string"]
        }
        r.check(v) !is ChRuleSet
    }
}
