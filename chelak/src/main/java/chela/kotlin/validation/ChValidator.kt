package chela.kotlin.validation

import chela.kotlin.viewmodel.ChViewModel
import kotlin.reflect.full.createType

internal typealias msg = (ChRuleSet)->String
abstract class ChValidator:ChViewModel(){
    companion object{
        @JvmStatic private val ruleSetType = ChRuleSet::class.createType()
        @JvmStatic internal val dmsg:msg = {"invalid value"}
        @JvmStatic internal val empty = ChRuleSet("", dmsg)
    }
    override operator fun set(k:String, v:Any):Boolean{
        val f = ref.setter[k]
        when {
            f == null -> throw Exception("invalid key:$k")
            v !is String -> throw Exception("invalid v:$v")
            f.returnType != ruleSetType -> throw Exception("invalid fieldType:$k")
            else ->{
                @Suppress("UNCHECKED_CAST")
                f.call(this, ChRuleSet(v, (ref.getter["_$k"]?.call(this) as? msg) ?: defaultMessage()))
            }
        }
        return true
    }
    open fun defaultMessage():msg = dmsg
}