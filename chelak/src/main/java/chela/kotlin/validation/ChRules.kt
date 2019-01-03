package chela.kotlin.validation

import chela.kotlin.Ch
import chela.kotlin.net.ChNet
import chela.kotlin.sql.ChSql

/**
 * This object check that all the rules are validate.
 */
object ChRules {
    /**
     * @param vararg Pair you want to validate.
     */
    @JvmStatic fun isOk(vararg kv:Pair<String, Any>) = kv.all {(k, v)->isOk(k, v) !is ChRuleSet}

    /**
     * @param k key
     * @param v value
     * If it passes validation, it returns its value.
     * Unless, it returns ChRuleSet type.
     */
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