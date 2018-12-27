package chela.kotlin.validation

import kotlin.reflect.KFunction

/**
 * Abstract class for application's data validation.
 */
abstract class ChRule{
    companion object{
        @JvmStatic internal val rules = mutableMapOf<String, Pair<KFunction<*>, ChRule>>()
    }
    init{
        /**
         * It stores child class method name.
         */
        @Suppress("LeakingThis")
        this::class.members.forEach{
            if(it.isFinal && it is KFunction){
                val k = it.name.toLowerCase()
                if (rules[k] != null) throw Exception("exist key:$k")
                rules[k] = it to this
            }
        }
    }
}