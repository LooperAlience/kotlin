package chela.kotlin.validation

import kotlin.reflect.full.createInstance

private val RULES = mutableMapOf<String, (List<String>)->ChRule>()
private val regArg = """\[(.+)\]""".toRegex()
private val emptyArg = listOf<String>()
class ChRuleSet(rule:String, private val msg: msg){
    companion object{
        @JvmStatic fun isOk(v:Any):Boolean = v !is ChRuleSet
    }
    private val rules = rule.split("|").filter{it.isNotBlank()}.map{v->
        regArg.find(v)?.let{
            val arg = it.groupValues[1].split(",").map{it.trim()}
            val k = regArg.replace(v, "").trim()
            val factory =RULES[k]
            return@map if(factory != null) factory(arg) else throw Exception("invalid rule:$k")
        } ?:
        RULES[v.trim()]?.let{it(emptyArg)} ?:
        throw Exception("invalid key:$v")
    }
    private var errorRule:ChRule = NoRule
    private var errorValue:Any = false
    internal fun check(v: Any):Any{
        var r = v
        var rule:ChRule = NoRule
        if(rules.any {
                r = it.check(r)
                rule = it
                return@any r is ChRule
            }){
            errorRule = rule
            errorValue = r
            return this
        }else return r
    }
}
private var isChRuleLoaded = false
sealed class ChRule{
    abstract fun check(v: Any): Any
    @JvmField internal var arg:List<String> = emptyArg
    init{
        @Suppress("LeakingThis")
        if(!isChRuleLoaded) {
            isChRuleLoaded = true
            ChRule::class.sealedSubclasses.forEach {cls->
                cls.simpleName?.let {
                    val k = it.toLowerCase()
                    if(RULES[k] == null){
                        val i = cls.java.getDeclaredField("INSTANCE").get(null) as? ChRule
                        if(i == null){
                            RULES[k] = {
                                val rule: ChRule = cls.createInstance()
                                rule.arg = it
                                rule
                            }
                        }else RULES[k] = {i}
                    }else throw Exception("exist key:$k")
                }
            }
        }
    }
}
object NoRule:ChRule(){override fun check(v: Any): Any = v}
object INT:ChRule(){
    override fun check(v: Any):Any = if(v is Int) v else this
}
object LONG:ChRule(){
    override fun check(v: Any):Any = if(v is Long) v else this
}
object FLOAT:ChRule(){
    override fun check(v: Any):Any = if(v is Float) v else this
}
object DOUBLE:ChRule(){
    override fun check(v: Any):Any = if(v is Double) v else this
}
object STRING:ChRule(){
    override fun check(v: Any):Any = if(v is String) v else this
}
object CHAR:ChRule(){
    override fun check(v: Any):Any = if(v is Char) v else this
}