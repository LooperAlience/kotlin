package chela.kotlin.i18n

import chela.kotlin.Ch
import chela.kotlin.model.Model
import org.json.JSONObject
import java.lang.Exception

object ChI18n:Model(){
    private var db = ""
    private var lang = ""
    private var ver = 1
    private val data = mutableMapOf<String, Map<String, String>>()
    @JvmStatic operator fun invoke(db:String, lang:String, ver:Int){this.db = db; this.lang = lang; this.ver = ver}
    @JvmStatic fun setLang(it:String){lang = it}
    @JvmStatic fun isLoaded() = Ch.sql[db]?.let{it.i("i18n_isLoaded") == 1} ?: false
    @JvmStatic fun load(files:List<String>) = files.map{ JSONObject(it) }.forEach { v ->
        v.keys().forEach { k ->
            Ch.sql[db]?.let{sql->
                val t = v.getJSONObject(k)
                sql.exec("i18n_add", "title" to k, "ver" to t.getInt("ver"))
                val data = t.getJSONObject("data")
                data.keys().forEach {lang->
                    val langData = data.getJSONObject(lang)
                    langData.keys().forEach {title->
                        sql.exec("i18ndata_add",
                            "i18n" to sql.lastId(),
                            "lang" to lang,
                            "title" to title,
                            "contents" to langData.getString(title)
                        )
                    }
                }
            }
        }
    }
    @JvmStatic fun map(key:String, ln:String = lang):Map<String, String> = data["$key.$ln"] ?: Ch.sql[db]?.let{
        val m = mutableMapOf<String, String>()
        it.select("i18n_get", false,"title" to key, "lang" to lang, "ver" to ver)?.forEach{_, a->
            m["${a[0]}"] = "${a[1]}"
        }
        if(m.isNotEmpty()) data["$key.$ln"] = m
        m
    } ?:mapOf()
    @JvmStatic fun get(v: List<String>):Any{
        if(v.size != 3) throw Exception("invalid i18n key(size != 3) $v")
        return map(v[1])[v[2]] ?: throw Exception("invalid key ${v[1]}.${v[2]}")
    }
}