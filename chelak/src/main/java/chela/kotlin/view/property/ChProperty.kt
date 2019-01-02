package chela.kotlin.view.property

import android.graphics.Color
import android.view.View

object ChProperty:Property(){
    @JvmStatic private val colors = mutableMapOf<String, Int>()
    @JvmStatic fun f(view:View, k:String, v:Any) = properties[k.toLowerCase()]?.let{it.first.call(it.second, view, v)}
    @JvmStatic fun color(v:String):Int{
        if(colors[v] == null) colors[v] = Color.parseColor(v)
        return colors[v]!!
    }
    @JvmStatic val event = PropEvent
    @JvmStatic val view = PropView
    @JvmStatic val text = PropText
    @JvmStatic val layout = PropLayout
    @JvmStatic val image = PropImage
}