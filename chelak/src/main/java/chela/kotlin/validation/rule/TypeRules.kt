package chela.kotlin.validation.rule

import chela.kotlin.validation.ChRule

class TypeRules: ChRule(){
    fun INT(v:Any, arg:List<String>) = if (v is Int) v else this
    fun LONG(v:Any, arg:List<String>) = if (v is Long) v else this
    fun FLOAT(v:Any, arg:List<String>) = if (v is Float) v else this
    fun DOUBLE(v:Any, arg:List<String>) = if (v is Double) v else this
    fun STRING(v:Any, arg:List<String>) = if (v is String) v else this
    fun CHAR(v:Any, arg:List<String>) = if (v is Char) v else this
}