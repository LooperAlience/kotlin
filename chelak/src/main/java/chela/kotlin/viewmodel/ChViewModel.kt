package chela.kotlin.viewmodel

import android.database.Cursor
import chela.kotlin.Ch
import chela.kotlin.core.*
import chela.kotlin.validation.ChRule
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object viewmodel{
    @JvmStatic internal val repo: MutableMap<String, ChViewModel> = HashMap()
    @JvmStatic fun viewmodel(v: List<String>): Any {
        if (v.isEmpty()) throw Exception("invalid list size == 0")
        repo[v[0]]?.let { return find(v, it) } ?: throw Exception("invalid key:" + v[0])
    }
    @JvmStatic fun record(v: List<String>, record: ChViewModel): Any {
        if (v.isEmpty()) throw Exception("invalid list size == 0")
        return find(v, record)
    }
    @JvmStatic private fun find(v: List<String>, it: ChViewModel): Any {
        var target = it
        var r: Any = 0
        for (idx in 1 until v.size) {
            r = target[v[idx]]
            if (r is ChViewModel) target = r
        }
        return r
    }
}
private abstract class Reg(r:String){
    private val re:Regex = r.toRegex()
    internal fun match(it: String):MatchResult? = re.find(it)
    internal fun cut(it:String):String = re.replaceFirst(it, "")
}
private object V:Reg("""^\s*(?:"((?:[^\\"]+|\\["\\bfnrt]|\\u[0-9a-fA-f]{4})*)"|`((?:[^`]+|\\[`\\bfnrt]|\\u[0-9a-fA-f]{4})*)`|""" + //1,2-string
    """(-?(?:0|[1-9]\d*)(?:\.\d+)(?:[eE][-+]?\d+)?)|(-?(?:0|[1-9]\d*))|(true|false)|""" + //3-double, 4-long, 5-bool
    """(?:\@\{([^}]+)\})|(?:\$\{([^}]+)\}))\s*""")
private object K:Reg("""^\s*(?:"([^":]*)"|([^:,\s"`]+)|`([^`:]*)`)\s*:\s*""")
private class St(val p: St?, val t:Char, v:String, val k: String = "", val i:Int = 0){
    @JvmField internal val v:String = v.trim()
    internal val key:String
        get(){
            var key = if(t == 'o') k else i.toString()
            var P = p
            while(P != null){
                key = "${if(P.t == 'o') P.k else P.i.toString()}.$key"
                P = P.p
            }
            return key._shift()
        }
    fun clone(p: St? = null, t:Char? = null, v:String? = null, k:String? = null, i:Int? = null): St =
        St(p ?: this.p, t ?: this.t, v ?: this.v, k ?: this.k, i ?: this.i)
}
abstract class ChViewModel{
    companion object {
        @JvmField val OBJECT = object{}
        @JvmField val ARRAY = object{}
        @JvmField val isTypeChecked = mutableSetOf<KClass<*>>()
    }
    init{
        @Suppress("LeakingThis")
        val cls = this::class
        if(!isTypeChecked.contains(cls)) cls.simpleName?.let{
            if(it == "Item") return@let
            try {
                val v = cls.java.getDeclaredField("INSTANCE")
                if(viewmodel.repo[it] == null) viewmodel.repo[it] = this else throw Exception("exist key:$it")
            }catch(e:Exception){

            }
        }
    }
    @JvmField @Suppress("LeakingThis")
    protected val ref = Ch.reflect.fields(this::class)
    @JvmField var isSet = false
    operator fun get(k:String):Any = ref.getter[k]?.call(this) ?: Ch.NONE
    open operator fun set(k:String, v:Any):Boolean = ref.setter[k]?.let {
        it.call(this, v)
        true
    } ?: false
    open fun viewmodel(k:String, v:List<String>):Boolean{throw Exception("override")}
    open fun record(k:String, v:List<String>):Boolean{throw Exception("override")}
    fun fromJson(v:String){
        isSet = true
        if(v.isBlank()) return
        mutableListOf(St(null, 'o', v.trim()))._allStack ch@{ c, st ->
            when(c.v[0]){
                ',' -> st += c.clone(v=c.v._shift(), k="", i=c.i + 1)
                '{' -> {
                    st += St(c, 'o', c.v._shift())
                    set(if(c.t == 'o') c.k else c.i.toString(), OBJECT)
                }
                '[' -> {
                    st += St(c, 'a', c.v._shift())
                    set(if(c.t == 'o') c.k else c.i.toString(), ARRAY)
                }
                '}', ']' -> c.v._shift()._notBlank{if(c.p != null) st += c.p.clone(v=it)}
                else ->
                    K.match(c.v)?.let{
                        st += c.clone(v= K.cut(c.v), k=it.groupValues[1]+it.groupValues[2]+it.groupValues[3])
                    }?:
                    V.match(c.v)?.let{
                        val key = c.key
                        var isOk = true
                        it.groups[1]?.let{isOk = set(key, it.value)} ?:
                        it.groups[2]?.let{isOk = set(key, it.value)} ?:
                        it.groups[3]?.let{isOk = set(key, it.value.toDouble())} ?:
                        it.groups[4]?.let{isOk = set(key, it.value.toLong())} ?:
                        it.groups[5]?.let{isOk = set(key, it.value.toBoolean())} ?:
                        it.groups[6]?.let{isOk = viewmodel(key, it.value.split("."))}
                        it.groups[7]?.let{isOk = record(key, it.value.split("."))}
                        V.cut(c.v)._notBlank{ v->st += c.clone(v=v)}
                        if(!isOk) return@ch false
                    }
            }
            return@ch true
        }
    }
}