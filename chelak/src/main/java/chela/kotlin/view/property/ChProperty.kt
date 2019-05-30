package chela.kotlin.view.property

import android.graphics.Color
import android.util.Log
import android.view.View
import chela.kotlin.Ch

object ChProperty:Property(){
    private val colors = mutableMapOf<String, Int>()
    private val plugin = mutableMapOf<String, (view:View, v:Any)->Unit>()
    fun add(k:String, block:(view:View, v:Any)->Unit){
        plugin[k] = block
    }
    fun f(view:View, k:String, v:Any) = properties[k.toLowerCase()]?.let{
        Log.i("ch", "chproperty $k -- $v--$view")
        it.first.call(it.second, view, if(v is Ch.Update) v.v else v)
    } ?: plugin[k.toLowerCase()]?.let{it(view, v)}
    fun color(v:String):Int{
        if(colors[v] == null) colors[v] = Color.parseColor(v)
        return colors[v]!!
    }
    val event = PropEvent
    val view = PropView
    val text = PropText
    val layout = PropLayout
    val image = PropImage
    val button = PropButton
    val switch = PropSwitch
}