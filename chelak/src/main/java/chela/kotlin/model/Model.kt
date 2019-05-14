package chela.kotlin.model

import chela.kotlin.Ch
import chela.kotlin.core.*
import chela.kotlin.crypto.ChCrypto
import chela.kotlin.regex.reK
import chela.kotlin.regex.reV
import chela.kotlin.view.ChStyleModel
import com.chela.annotation.EX
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class Model(isRegister:Boolean = true, name:String? = null){
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
        @EX private val isTypeChecked = mutableSetOf<KClass<*>>()
    }
    init{val cls = this::class
        if(!isTypeChecked.contains(cls)){
            isTypeChecked.add(cls)
            if(isRegister) _try{cls.java.getDeclaredField("INSTANCE")}?.let{
                (name ?: cls.simpleName)?.let {
                    if (ChModel.repo.containsKey(it)) throw Throwable("exist key:$it")
                    else ChModel.repo[it] = this
                }
            }
        }
    }
    @EX val ref by Ch.ulazy{ChReflect.fields(this)}
    @EX @JvmField var isSet = false
    open operator fun get(k:String) = ref.getter[k]?.call(this) ?: Ch.NONE
    open operator fun set(k:String, v:Any) = ref.setter[k]?.let {
        it.call(this, v)
        true
    } ?: false
    open fun viewmodel(k:String, v:List<String>):Boolean{throw Exception("override")}
    open fun record(k:String, v:List<String>):Boolean{throw Exception("override")}
    open fun stringify():String = ""
    fun fromJson(v:String){
        isSet = true
        if(v.isBlank()) return
        mutableListOf(St(null, 'o', v.trim()))._allStack ch@{ c, st ->
            when(c.v[0]){
                ',' -> st += c.clone(v=c.v._shift(), k="", i=c.i + 1)
                '{' -> {
                    st += St(c, 'o', c.v._shift())
                    set(if(c.t == 'o') c.k else c.i.toString(), Ch.OBJECT)
                }
                '[' -> {
                    st += St(c, 'a', c.v._shift())
                    set(if(c.t == 'o') c.k else c.i.toString(), Ch.ARRAY)
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
                            it.groups[7]?.let{record(key, ("_." + it.value).split("."))} ?: true
                        reV.cut(c.v)._notBlank{ v->st += c.clone(v=v)}
                        if(!isOk) return@ch false
                    }
            }
            return@ch true
        }
    }
}