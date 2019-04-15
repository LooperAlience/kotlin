package chela.kotlin.i18n

import android.util.Log
import chela.kotlin.resource.I18n

object ChI18n{
    private var lang = ""
    private val items = mutableMapOf<String, I18n>()

    operator fun invoke(ln:String){lang = ln}
    operator fun invoke() = lang

    fun add(k:String, res:I18n){items[k] = res}
    fun remove(k:String){items.remove(k)}

    operator fun get(k:String):String = get("i18n.$k".split(".").map {it.trim()})
    operator fun get(k:List<String>):String{
        if(k.size < 3) return "no data:$k"
        val (_, key, subKey) = k
        val i = items[key] ?: return "no data:$key"
        val ln =  if(i.isOne.isNotBlank()) i.isOne
            else if(k.size == 4) k[3]
            else lang
        if(ln.isBlank()) return "no setting_language"
        return i.data[ln]?.get(subKey) ?: "no data:$k"
    }
}