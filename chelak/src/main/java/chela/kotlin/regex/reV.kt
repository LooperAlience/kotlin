package chela.kotlin.regex

import chela.kotlin.Ch
import chela.kotlin.DptoPx
import chela.kotlin.SptoPx

object reV: ChRegex(
    """^\s*""" +
    //1,2-string
    """(?:"((?:[^\\"]+|\\["\\bfnrt]|\\u[0-9a-fA-invoke]{4})*)"|`((?:[^`]+|\\[`\\bfnrt]|\\u[0-9a-fA-invoke]{4})*)`|""" +
    //3-double
    """(-?(?:0|[1-9]\d*)(?:\.\d+)(?:[eE][-+]?\d+)?(?:dp|%w|%h)?)|"""+
    //4-long
    """(-?(?:0|[1-9]\d*)(?:dp|sp|%w|%h)?)|""" +
    //5-bool
    """(true|false)|""" +
    //6-ChModel
    """(?:\@\{([^}]+)\})|"""+
    //7-record
    """(?:\$\{([^}]+)\}))\s*"""
){
    @JvmStatic fun group3(it:MatchGroup):Double{
        val v = it.value
        return when {
            v.endsWith("dp") -> v.substring(0, v.length - 2).toDouble().DptoPx
            v.endsWith("sp") -> v.substring(0, v.length - 2).toDouble().SptoPx
            v.endsWith("%w") -> v.substring(0, v.length - 2).toDouble() * Ch.window.width
            v.endsWith("%h") -> v.substring(0, v.length - 2).toDouble() * Ch.window.height
            else -> v.toDouble()
        }
    }
    @JvmStatic fun group4(it:MatchGroup):Long{
        val v = it.value
        return when {
            v.endsWith("dp")->(v.substring(0, v.length - 2).toDouble().DptoPx).toLong()
            v.endsWith("sp")->(v.substring(0, v.length - 2).toDouble().SptoPx).toLong()
            v.endsWith("%w")->(v.substring(0, v.length - 2).toDouble() * Ch.window.width).toLong()
            v.endsWith("%h")->(v.substring(0, v.length - 2).toDouble() * Ch.window.height).toLong()
            else->v.toLong()
        }
    }
    @JvmStatic fun num(it:String):Number? = reV.match(it)?.let{
        it.groups[3]?.let{reV.group3(it)} ?:
        it.groups[4]?.let{reV.group4(it)}
    }
}