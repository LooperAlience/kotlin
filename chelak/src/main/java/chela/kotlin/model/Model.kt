package chela.kotlin.model

import chela.kotlin.Ch
import chela.kotlin.PxtoDp
import chela.kotlin.PxtoSp
import chela.kotlin.core._allStack
import chela.kotlin.core._notBlank
import chela.kotlin.core._shift
import chela.kotlin.regex.reK
import chela.kotlin.regex.reV
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
abstract class Model{
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
    companion object {
        @JvmField val OBJECT = object{}
        @JvmField val ARRAY = object{}
        @JvmField val isTypeChecked = mutableSetOf<KClass<*>>()
    }
    init{
        @Suppress("LeakingThis")
        val cls = this::class
        if(!isTypeChecked.contains(cls)) cls.simpleName?.let{
            isTypeChecked.add(cls)
            try {
                cls.java.getDeclaredField("INSTANCE")
                if(ChModel.repo[it] == null) ChModel.repo[it] = this else throw Exception("exist key:$it")
            }catch(e:Exception){
            }
        }
    }
    @JvmField @Suppress("LeakingThis")
    val ref = Ch.reflect.fields(this::class)
    @JvmField var isSet = false
    open operator fun get(k:String):Any = ref.getter[k]?.call(this) ?: Ch.NONE
    open operator fun set(k:String, v:Any):Boolean = ref.setter[k]?.let {
        it.call(this, v)
        true
    } ?: false
    open fun viewmodel(k:String, v:List<String>):Boolean{throw Exception("override")}
    open fun record(k:String, v:List<String>):Boolean{throw Exception("override")}
    fun stringify():String{
        val r = mutableListOf<String>()
        this::class.memberProperties.forEach{p->
            var k = ""
            val v = p.getter.call(this)
            when(
                p.findAnnotation<Ch.STRING>()?.let {k = it.name;'s'} ?:
                p.findAnnotation<Ch.NUMBER>()?.let {k = it.name;'n'} ?:
                p.findAnnotation<Ch.BOOLEAN>()?.let {k = it.name;'b'} ?:
                p.findAnnotation<Ch.SHA256>()?.let {k = it.name;'2'} ?:
                p.findAnnotation<Ch.OUT>()?.let {k = it.name
                    when(v) {
                        is String -> 's'
                        is Boolean -> 'b'
                        is Number -> 'n'
                        is Model -> 'v'
                        else -> '-'
                    }
                }
            ) {
                's'->"\"${if(v is String) v.replace("\"", "\\\"") else "$v"}\""
                '2'->"\"${if(v is String) Ch.crypto.sha256(v) else ""}\""
                'n'->"${if(v is Number) v else 0}"
                'b'->"${if(v is Boolean) v else false}"
                'v'-> (v as Model).stringify()
                else -> null
            }?.let {r += "\"${if (k.isNotBlank()) k else p.name}\":$it"}
        }
        return "{${r.joinToString(",")}}"
    }
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
                    reK.match(c.v)?.let{
                        st += c.clone(v= reK.cut(c.v), k=it.groupValues[1]+it.groupValues[2]+it.groupValues[3])
                    }?:
                    reV.match(c.v)?.let{
                        val key = c.key
                        val isOk = it.groups[1]?.let{set(key, it.value)} ?:
                            it.groups[2]?.let{set(key, it.value)} ?:
                            it.groups[3]?.let{set(key, reV.group3(it))} ?:
                            it.groups[4]?.let{set(key, reV.group4(it))} ?:
                            it.groups[5]?.let{set(key, it.value.toBoolean())} ?:
                            it.groups[6]?.let{viewmodel(key, it.value.split("."))} ?:
                            it.groups[7]?.let{record(key, it.value.split("."))} ?: true
                        reV.cut(c.v)._notBlank{ v->st += c.clone(v=v)}
                        if(!isOk) return@ch false
                    }
            }
            return@ch true
        }
    }
}