package chela.kotlin.viewmodel

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

internal val properties = ChProperties.values().map{it.name.toLowerCase() to it}.toMap()
private val colors = mutableMapOf<String, Int>()
private fun color(v:String):Int{
    if(colors[v] == null) colors[v] = Color.parseColor(v)
    return colors[v]!!
}
enum class ChProperties{
    Click{override fun f(view:View, v:Any){
        if(v !is View.OnClickListener) return
        view.isClickable = true
        view.setOnClickListener(v)
    }},
    Display{override fun f(view:View, v:Any){
        if(v is Boolean) view.visibility = if(v) View.VISIBLE else View.GONE
    }},
    Visibility{override fun f(view:View, v:Any){
        if(v is Boolean) view.visibility = if(v) View.VISIBLE else View.GONE
        else if(v is Number) view.visibility = v.toInt()
    }},
    Background{override fun f(view:View, v:Any){
        if(v !is String) return
        view.setBackgroundColor(color(v))
    }},
    Shadow{override fun f(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.elevation = v.toFloat()
    }},
    XP{override fun f(view:View, v:Any){
        if(v is Number) view.translationX = v.toFloat() / (view.parent as ViewGroup).width.toFloat()
    }},
    X{override fun f(view:View, v:Any){
        if(v is Number) view.translationX = v.toFloat()
        else if(v is String){
            when (v) {
                "parentWidth" -> if (view.parent != null) view.translationX = (view.parent as ViewGroup).width.toFloat()
            }
        }
    }},
    Y{override fun f(view:View, v:Any){
        if(v !is Number) return
        view.translationY = v.toFloat()
    }},
    Z{override fun f(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.translationZ = v.toFloat()
    }},
    Text{override fun f(view:View, v:Any){
        if(view !is TextView) return
        view.text = v as String
    }},
    TextSize{override fun f(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textSize = v.toFloat()
    }},
    TextColor{override fun f(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setTextColor(color(v))
    }};
    abstract fun f(view:View, v:Any)
}