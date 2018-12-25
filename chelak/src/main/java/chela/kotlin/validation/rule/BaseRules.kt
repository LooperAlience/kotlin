package chela.kotlin.validation.rule

import chela.kotlin.validation.ChRule

class BaseRules: ChRule(){
    fun NoRule(v:Any, arg:List<String>) = v
    fun MinLength(v:Any, arg:List<String>) = if (v is String && arg.size == 1 && v.length >= arg[0].toInt()) v else this
    fun MaxLength(v:Any, arg:List<String>) = if (v is String && arg.size == 1 && v.length <= arg[0].toInt()) v else this
    fun LessThan(v:Any, arg:List<String>) = if (v is Number && arg.size == 1 && v.toDouble() < arg[0].toDouble()) v else this
    fun GreaterThan(v:Any, arg:List<String>) = if (v is Number && arg.size == 1 && v.toDouble() > arg[0].toDouble()) v else this
    fun Range(v:Any, arg:List<String>) = if(v is Number && arg.size == 2 && arg[0].toDouble() <= v.toDouble() && v.toDouble() <= arg[1].toDouble()) v else this
    fun Equal(v:Any, arg:List<String>) = when{
        arg.size != 1 -> this
        v is Number -> if (v.toDouble() == arg[0].toDouble()) v else this
        v is String -> if (v == arg[0]) v else this
        v is Boolean -> if (v == arg[0].toBoolean()) v else this
        else -> this
    }
    fun In(v:Any, arg:List<String>) = when(v){
        is String->if(arg.contains(v)) v else this
        is Int->if(arg.map{it.toInt()}.contains(v)) v else this
        is Long->if(arg.map{it.toLong()}.contains(v)) v else this
        is Float->if(arg.map{it.toFloat()}.contains(v)) v else this
        is Double->if(arg.map{it.toDouble()}.contains(v)) v else this
        else->this
    }
    fun NotIn(v:Any, arg:List<String>) = when(v){
        is String->if(!arg.contains(v)) v else this
        is Int->if(!arg.map{it.toInt()}.contains(v)) v else this
        is Long->if(!arg.map{it.toLong()}.contains(v)) v else this
        is Float->if(!arg.map{it.toFloat()}.contains(v)) v else this
        is Double->if(!arg.map{it.toDouble()}.contains(v)) v else this
        else->this
    }
}