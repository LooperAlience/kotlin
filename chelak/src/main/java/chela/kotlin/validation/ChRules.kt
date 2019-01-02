package chela.kotlin.validation

import chela.kotlin.Ch
import chela.kotlin.net.ChNet
import chela.kotlin.sql.ChSql

object ChRules {
    @JvmStatic fun isOk(vararg kv:Pair<String, Any>) = kv.all {(k, v)->isOk(k, v) !is ChRuleSet}

    @JvmStatic fun isOk(k:String, v:Any):Any = when{
        k.isBlank() -> ChRuleSet["string"]
        !k.contains(".") -> ChRuleSet[k]
        else ->
            ChSql.rulesets[k] ?:
            ChNet.ruleSet[k] ?:
            Ch.model.get(k.split(".")) as? ChRuleSet ?:
            ChRuleSet["string"]
    }.check(v)
}