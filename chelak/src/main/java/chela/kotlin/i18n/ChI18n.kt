package chela.kotlin.i18n

import chela.kotlin.resource.I18n

object ChI18n{
    @JvmStatic private var lang = ""
    @JvmStatic private val items = mutableMapOf<String, I18n>()

    @JvmStatic operator fun invoke(ln:String){lang = ln}
    @JvmStatic operator fun invoke() = lang

    @JvmStatic fun add(k:String, res:I18n){items[k] = res}
    @JvmStatic fun remove(k:String){items.remove(k)}

    @JvmStatic operator fun get(k:String):String = get("i18n.$k".split(".").map {it.trim()})
    @JvmStatic operator fun get(k:List<String>):String{
        if(k.size < 3) return "no data:$k"
        val (_, key, subKey) = k
        val i = items[key] ?: return "no data:$key"
        val ln = if(k.size == 4) k[3] else ""
        return i.data[
                if(i.isOne.isNotBlank()) i.isOne
                else if(ln != "") ln
                else lang
            ]?.get(subKey) ?: "no data:$k"
    }

}