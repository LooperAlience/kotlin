package chela.kotlin.view

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import chela.kotlin.core._forValue
import chela.kotlin.core._list
import chela.kotlin.regex.reV
import org.json.JSONObject

object ChDrawable{
    sealed class Param{
        class StrokeWidth(val width:Int):Param()
        class StrokeColor(val color:String):Param()
        class Solid(val color:String):Param()
        class Gradient(val colors:IntArray):Param()
        class Corner(val radius:Float):Param()
        object Line:Param()
        object Rect:Param()
        object Oval:Param()
        object Ring:Param()
        object None:Param()
        object Linear:Param()
        object Radial:Param()
        object Sweep:Param()
        object BL_TR:Param()
        object B_T:Param()
        object BR_TL:Param()
        object L_R:Param()
        object TL_BR:Param()
        object T_B:Param()
        object TR_BL:Param()
    }
    @JvmStatic private val drawables = mutableMapOf<String, Drawable>()
    @JvmStatic fun drawable(k:String):Drawable? = drawables[k]
    @JvmStatic fun remove(k:String) = drawables.remove(k)
    @JvmStatic fun shape(k:String, obj:JSONObject):Drawable{
        val arg = mutableListOf<Param>()
        obj._forValue { key, v ->
            when(key.toLowerCase()) {
                "strokewidth" -> {
                    if (v is Number) arg += Param.StrokeWidth(v.toInt())
                    else if (v is String) reV.num(v)?.let {arg += Param.StrokeWidth(it.toInt())}
                }
                "corner" -> {
                    if (v is Number) arg += Param.Corner(v.toFloat())
                    else if (v is String) reV.num(v)?.let {arg += Param.Corner(it.toFloat())}
                }
                "strokecolor" -> arg += Param.StrokeColor("$v")
                "solid" -> arg += Param.Solid("$v")
                else -> Param::class.sealedSubclasses.
                    find { it.simpleName?.toLowerCase() == key.toLowerCase() }?.
                    let {it.objectInstance}?.let{arg += it}
            }
        }
        obj._list<String>("gradient")?.map{Color.parseColor(it)}?.let {
            if(it.isNotEmpty()) arg += Param.Gradient(it.toIntArray())
        }
        return shape(k, *arg.toTypedArray())
    }
    @JvmStatic fun shape(vararg arg:Param):Drawable = shape("", *arg)
    @JvmStatic fun shape(k:String, vararg arg:Param):Drawable{
        drawables[k]?.let{return it}
        var width = 0
        var stroke = 0
        var solid = 0
        var isSolid = false
        var gradient = intArrayOf()
        var isCorner = false
        var corner = 0F
        var shape:Param = Param.None
        var type = GradientDrawable.LINEAR_GRADIENT
        var orient = GradientDrawable.Orientation.LEFT_RIGHT
        arg.forEach {
            when(it){
                is Param.StrokeWidth -> width = it.width
                is Param.StrokeColor -> stroke = Color.parseColor(it.color)
                is Param.Solid ->{
                    isSolid = true
                    solid = Color.parseColor(it.color)
                }
                is Param.Gradient -> gradient = it.colors
                is Param.Corner ->{
                    isCorner = true
                    corner = it.radius
                }
                is Param.Ring,is Param.Oval,is Param.Rect,is Param.Line ->shape = it
                is Param.Linear ->type = GradientDrawable.LINEAR_GRADIENT
                is Param.Radial ->type = GradientDrawable.RADIAL_GRADIENT
                is Param.Sweep ->type = GradientDrawable.SWEEP_GRADIENT
                is Param.BL_TR ->orient = GradientDrawable.Orientation.BL_TR
                is Param.B_T ->orient = GradientDrawable.Orientation.BOTTOM_TOP
                is Param.BR_TL ->orient = GradientDrawable.Orientation.BR_TL
                is Param.L_R ->orient = GradientDrawable.Orientation.LEFT_RIGHT
                is Param.TL_BR ->orient = GradientDrawable.Orientation.TL_BR
                is Param.T_B ->orient = GradientDrawable.Orientation.TOP_BOTTOM
                is Param.TR_BL ->orient = GradientDrawable.Orientation.TR_BL
            }
        }
        val key = if(k != "") k
            else "shape-$width:$stroke:$solid:$isSolid:$gradient:$isCorner:$corner:$shape:$type:$orient"
        drawables[key]?.let{return it}
        if (isSolid) gradient = intArrayOf(solid, solid)
        val gd = GradientDrawable(orient, gradient)
        gd.gradientType = type
        if (shape != Param.None) gd.shape = when (shape) {
            is Param.Line -> GradientDrawable.LINE
            is Param.Oval -> GradientDrawable.OVAL
            is Param.Ring -> GradientDrawable.RING
            is Param.Rect -> GradientDrawable.RECTANGLE
            else -> GradientDrawable.RECTANGLE
        }
        if (isCorner) gd.cornerRadius = corner
        if (width > 0) gd.setStroke(width, stroke)
        drawables[key] = gd
        return gd
    }
}