package chela.kotlin.viewmodel

import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.TextView
private val colors = mutableMapOf<String, Int>()
private fun color(v:String):Int{
    if(colors[v] == null) colors[v] = Color.parseColor(v)
    return colors[v]!!
}
sealed class Property{abstract fun f(view:View, v:Any)}
object ChProperty{
    @JvmStatic val properties = mutableMapOf<String, Property>()
    init {
        val check = mutableSetOf<String>()
        Property::class.sealedSubclasses.map { cls ->
            cls.simpleName?.let {
                val k = it.toLowerCase()
                if (check.contains(k)) throw Exception("exist key:$k")
                check += k
                cls.objectInstance?.let {
                    properties[k] = it
                } ?: throw Exception("exist key:$k")
            }
        }
    }
    @JvmStatic fun f(view:View, k:String, v:Any) = properties[k.toLowerCase()]?.f(view, v)
    @JvmStatic val click = Click
    @JvmStatic val display = Display
    @JvmStatic val visibility = Visibility
    @JvmStatic val background = Background
    @JvmStatic val shadow = Shadow
    @JvmStatic val x = X
    @JvmStatic val y = Y
    @JvmStatic val z = Z
    @JvmStatic val text = Text
    @JvmStatic val textSize = TextSize
    @JvmStatic val textColor = TextColor
}
object Click:Property(){override fun f(view:View, v:Any){
    if(v !is View.OnClickListener) return
    view.isClickable = true
    view.setOnClickListener(v)
}}
object Display:Property(){override fun f(view:View, v:Any){
    if(v is Boolean) view.visibility = if(v) View.VISIBLE else View.GONE
}}
object Visibility:Property(){override fun f(view:View, v:Any){
    if(v is Boolean) view.visibility = if(v) View.VISIBLE else View.GONE
    else if(v is Number) view.visibility = v.toInt()
}}
object Background:Property(){override fun f(view:View, v:Any){
    if(v !is String) return
    view.setBackgroundColor(color(v))
}}
object Shadow:Property(){override fun f(view:View, v:Any){
    if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
    view.elevation = v.toFloat()
}}
object X:Property(){override fun f(view:View, v:Any){
    if(v is Number) view.translationX = v.toFloat()
}}
object Y:Property(){override fun f(view:View, v:Any){
    if(v !is Number) return
    view.translationY = v.toFloat()
}}
object Z:Property(){override fun f(view:View, v:Any){
    if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
    view.translationZ = v.toFloat()
}}

object Text: Property(){override fun f(view: View, v:Any){
    if(view !is TextView) return
    view.text = v as String
}}
object TextSize: Property(){override fun f(view: View, v:Any){
    if(v !is Number || view !is TextView) return
    view.textSize = v.toFloat()
}}
object TextColor: Property(){override fun f(view: View, v:Any){
    if(v !is String || view !is TextView) return
    view.setTextColor(color(v))
}}