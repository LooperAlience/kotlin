package chela.kotlin.validation

import kotlin.reflect.KFunction

/**
 * Abstract class for application's data validation.
 */
typealias ruleF = (arg:List<String>)->(v:Any)->Any
abstract class ChRule{
    companion object{
        @JvmStatic internal val _defined = mutableMapOf<String, ruleF>()
        @JvmStatic operator fun get(k:String):ruleF = _defined[k] ?: throw Exception("invalid rule:$k")
    }
    init{
        /**
         * It stores child class method name.
         */
        @Suppress("LeakingThis")
        this::class.members.forEach{f->
            if(f.isFinal && f is KFunction){
                val k = f.name.toLowerCase()
                if (_defined[k] != null) throw Exception("exist rule:$k")
                _defined[k] = { arg:List<String>->{ v:Any->f.call(this, v, arg)!!}}
                ChRuleSet.set(k, k, false)
            }
        }
    }
}