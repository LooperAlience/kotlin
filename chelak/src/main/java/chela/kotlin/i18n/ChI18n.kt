package chela.kotlin.i18n

import chela.kotlin.Ch
import chela.kotlin.core.*
import chela.kotlin.model.Model
import org.json.JSONObject
import java.lang.Exception

object ChI18n:Model(){
    @JvmStatic private val GROUP_ADD = "i18n_add"
    @JvmStatic private val KEY_ADD = "i18ndata_add"
    @JvmStatic private val KEY_GET = "i18n_get"
    @JvmStatic private val DB_CHECK = "i18n_isLoaded"

    @JvmStatic private val DATA = "data"
    @JvmStatic private val VER = "ver"
    @JvmStatic private val TITLE = "title"
    @JvmStatic private val CONTENTS = "contents"
    @JvmStatic private val I18N = "i18n"
    @JvmStatic private val LANG = "lang"


    @JvmStatic private var db = ""
    @JvmStatic private var lang = ""
    @JvmStatic private val data = mutableMapOf<String, Map<String, String>>()
    @JvmStatic operator fun invoke(db:String, lang:String){this.db = db; this.lang = lang;}
    @JvmStatic fun lang(it:String){lang = it}
    @JvmStatic fun isLoaded() = Ch.sql[db]?.let{it.i(DB_CHECK) == 1} ?: false
    @JvmStatic fun load(files:List<String>) = files.forEach{v->
        _try{JSONObject(v)}?.let{v->
            v._forObject { k, obj ->
                Ch.sql[db]?.let ch@{ sql ->
                    val ver = obj._int(VER) ?: return@ch
                    sql.exec(GROUP_ADD, TITLE to k, VER to ver)
                    val id = sql.lastId()
                    obj._forObject(DATA) { lang, langData ->
                        langData._forString { title, contents ->
                            sql.exec(KEY_ADD,
                                I18N to id,
                                LANG to lang,
                                TITLE to title,
                                CONTENTS to contents
                            )
                        }
                    }
                }
            }
        }
    }
    @JvmStatic fun map(key:String, ver:Int, ln:String = lang) = data["$key.$ln.$ver"] ?: Ch.sql[db]?.let{
        val m = mutableMapOf<String, String>()
        it.select(KEY_GET, false,TITLE to key, LANG to lang, VER to ver)?.forEach{_, a->
            m["${a[0]}"] = "${a[1]}"
        }
        if(m.isNotEmpty()) data["$key.$ln.$ver"] = m
        m
    }
    @JvmStatic fun get(v: List<String>):Any{
        if(v.size != 4) throw Exception("invalid i18n key(size != 3) $v")
        return map(v[1], v[3].toInt())?.get(v[2]) ?: throw Exception("invalid key ${v[1]}.${v[2]}.${v[3]}")
    }
}