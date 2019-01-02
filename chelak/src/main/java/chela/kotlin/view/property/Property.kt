package chela.kotlin.view.property

import kotlin.reflect.KFunction

internal val properties = mutableMapOf<String, Pair<KFunction<*>,Property>>()
abstract class Property{
    init{
        @Suppress("LeakingThis")
        this::class.members.forEach{
            if(it.isFinal && it is KFunction){
                val k = it.name.toLowerCase()
                if (properties[k] == null) properties[k] = it to this
                else throw Exception("exist key:$k")
            }
        }
    }
}