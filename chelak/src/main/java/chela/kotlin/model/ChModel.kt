package chela.kotlin.model

import chela.kotlin.i18n.ChI18n

object ChModel{
    @JvmStatic internal val repo: MutableMap<String, Model> = HashMap()
    @JvmStatic fun get(v: List<String>):Any{
        if(v.isEmpty()) throw Exception("invalid list size == 0")
        if(v[0] == "i18n") return ChI18n.get(v)
        repo[v[0]]?.let { return find(v, it) } ?: throw Exception("invalid key:" + v[0])
    }
    @JvmStatic fun record(v: List<String>, record: Model): Any {
        if (v.isEmpty()) throw Exception("invalid list size == 0")
        return find(v, record)
    }
    @JvmStatic private fun find(v: List<String>, it: Model): Any {
        var model: Model? = it
        var list:MutableList<Any>? = null
        var r: Any = 0
        for(idx in 1 until v.size) {
            r = model?.get(v[idx]) ?: list?.get(v[idx].toInt()) ?: throw Exception("invalid key:${v[idx]} in $v")
            when(r){
                is Model ->{
                    model = r
                    list = null
                }
                is List<*>->{
                    model = null
                    @Suppress("UNCHECKED_CAST")
                    list = r as MutableList<Any>
                }
            }
        }
        return r
    }
}

