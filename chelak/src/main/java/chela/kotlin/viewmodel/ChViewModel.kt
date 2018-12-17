package chela.kotlin.viewmodel

import chela.kotlin.core.*

private val repo:MutableMap<String, ChViewModel> = HashMap()
fun viewmodels(it:String): ChViewModel? = repo[it]
fun vmFind(v:List<String>):Any{
    if(v.isEmpty()) throw Exception("invalid list size == 0")
    repo[v[0]]?.let{return find(v, it) } ?: throw Exception("invalid key:" + v[0])
}
fun recordFind(v:List<String>, record: ChViewModel):Any{
    if(v.isEmpty()) throw Exception("invalid list size == 0")
    return find(v, record)
}
private fun find(v:List<String>, it: ChViewModel):Any{
    var target = it
    var r:Any = 0
    for(idx in 1 until v.size){
        r = target[v[idx]]
        if(r is ChViewModel) target = r
    }
    return r
}
private enum class Reg(r:String){
    V("""^\s*(?:"((?:[^\\"]+|\\["\\bfnrt]|\\u[0-9a-fA-f]{4})*)"|`((?:[^`]+|\\[`\\bfnrt]|\\u[0-9a-fA-f]{4})*)`|""" + //1,2-string
            """(-?(?:0|[1-9]\d*)(?:\.\d+)(?:[eE][-+]?\d+)?)|(-?(?:0|[1-9]\d*))|(true|false)|""" + //3-double, 4-long, 5-bool
            """(?:\@\{([^}]+)\})|(?:\$\{([^}]+)\}))\s*"""), //6-viewmodel, 7-record
        K("""^\s*(?:"([^":]*)"|([^:,\s"`]+)|`([^`:]*)`)\s*:\s*""");
    val re: Regex = r.toRegex()
    fun match(it: String):MatchResult? = re.find(it)
    fun cut(it:String):String = re.replaceFirst(it, "")
}
private class St(val p: St?, val t:Char, v:String, val k: String = "", val i:Int = 0){
    val v:String = v.trim()
    val key:String
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
    init{
        @Suppress("LeakingThis")
        this::class.simpleName?.let ch@{
            if(it == "ChScannedItem") return@ch
            if(repo[it] == null) repo[it] = this else throw Exception("exist key:$it")
        }
    }
    var isSet = false
    open operator fun set(k:String, v:Any):Boolean{throw Exception("override")}
    open fun viewmodel(k:String, v:List<String>):Boolean{throw Exception("override")}
    open fun record(k:String, v:List<String>):Boolean{throw Exception("override")}
    @Suppress("LeakingThis")
    private val ref = reflectFields(this::class)
    operator fun get(k:String):Any = ref[k]?.call(this) ?: NONE
    fun l(k:String):Long = get(k) as Long
    fun d(k:String):Double = get(k) as Double
    fun s(k:String):String = get(k) as String
    fun b(k:String):Boolean = get(k) as Boolean
    fun fromJson(v:String){
        isSet = true
        if(v.isBlank()) return
        mutableListOf(St(null, 'o', v.trim()))._allStack ch@{ c, st ->
            when(c.v[0]){
                ',' -> st += c.clone(v=c.v._shift(), k="", i=c.i + 1)
                '{' -> st += St(c, 'o', c.v._shift())
                '[' -> st += St(c, 'a', c.v._shift())
                '}', ']' -> c.v._shift()._notBlank{if(c.p != null) st += c.p.clone(v=it)}
                else ->
                    Reg.K.match(c.v)?.let{
                        st += c.clone(v= Reg.K.cut(c.v), k=it.groupValues[1]+it.groupValues[2]+it.groupValues[3])
                    }?:
                    Reg.V.match(c.v)?.let{
                        val key = c.key
                        var isOk = true
                        it.groups[1]?.let{isOk = set(key, it.value)} ?:
                        it.groups[2]?.let{isOk = set(key, it.value)} ?:
                        it.groups[3]?.let{isOk = set(key, it.value.toDouble())} ?:
                        it.groups[4]?.let{isOk = set(key, it.value.toLong())} ?:
                        it.groups[5]?.let{isOk = set(key, it.value.toBoolean())} ?:
                        it.groups[6]?.let{isOk = viewmodel(key, it.value.split("."))}
                        it.groups[7]?.let{isOk = record(key, it.value.split("."))}
                        Reg.V.cut(c.v)._notBlank{ v->st += c.clone(v=v)}
                        if(!isOk) return@ch false
                    }
            }
            return@ch true
        }
    }
}