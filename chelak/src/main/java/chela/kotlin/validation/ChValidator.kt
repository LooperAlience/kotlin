package chela.kotlin.validation

import chela.kotlin.viewmodel.ChViewModel
import kotlin.reflect.full.createType

typealias msg = (ChRuleSet)->String

private val dmsg:msg = {"invalid value"}
private val ruleSetType = ChRuleSet::class.createType()
abstract class ChValidator:ChViewModel(){
    companion object{
        @JvmStatic val empty = ChRuleSet("", dmsg)
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
object ChTypeValidator:ChValidator(){
    @JvmStatic val int = ChRuleSet("int", dmsg)
    @JvmStatic val long = ChRuleSet("long", dmsg)
    @JvmStatic val float = ChRuleSet("float", dmsg)
    @JvmStatic val double = ChRuleSet("double", dmsg)
    @JvmStatic val string = ChRuleSet("string", dmsg)
    @JvmStatic val char = ChRuleSet("char", dmsg)
}