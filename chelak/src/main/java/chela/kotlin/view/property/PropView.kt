package chela.kotlin.view.property

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import chela.kotlin.android.ChApp
import chela.kotlin.regex.reV
import chela.kotlin.view.ChDrawable

object PropView:Property(){
    fun tag(view:View, v:Any){
        if(v !is String) return
        view.tag = v
    }
    fun isEnabled(view:View, v:Any){
        if(v !is Boolean) return
        view.isEnabled = v
    }
    fun visibility(view:View, v:Any){
        view.visibility = when(v){
            is Boolean->if(v) View.VISIBLE else View.GONE
            is Number-> v.toInt()
            else -> View.VISIBLE
        }
    }
    fun background(view:View, v:Any){
        when(v){
            is String ->{
                when(v[0]){
                    '#'->view.setBackgroundColor(ChProperty.color(v))
                    else-> {
                        ChDrawable.drawable(v)?.let { view.background = it }
                            ?: view.setBackgroundResource(ChApp.resDrawable(v))
                    }
                }
            }
            is Int -> view.setBackgroundResource(v)
            is Drawable -> view.background = v
            is Bitmap -> view.background = ChApp.bitmap2Drawable(v)
        }
    }
    fun shadow(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.elevation = v.toFloat()
    }
    fun x(view:View, v:Any){
        if(v !is Number) return
        view.translationX = v.toFloat()
    }
    fun y(view:View, v:Any){
        if(v !is Number) return
        view.translationY = v.toFloat()
    }
    fun z(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.translationZ = v.toFloat()
    }
    fun scaleX(view:View, v:Any){
        if(v !is Number) return
        view.scaleX = v.toFloat()
    }
    fun scaleY(view:View, v:Any){
        if(v !is Number) return
        view.scaleY = v.toFloat()
    }
    fun rotation(view:View, v:Any){
        if(v !is Number) return
        view.rotation = v.toFloat()
    }
    fun alpha(view:View, v:Any){
        if(v !is Number) return
        view.alpha = v.toFloat()
    }
    fun paddingStart(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(v.toInt(), view.paddingTop, view.paddingEnd, view.paddingBottom)
    }
    fun paddingEnd(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, view.paddingTop, v.toInt(), view.paddingBottom)
    }
    fun paddingTop(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, v.toInt(), view.paddingEnd, view.paddingBottom)
    }
    fun paddingBottom(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, view.paddingTop, view.paddingEnd, v.toInt())
    }
    fun padding(view:View, v:Any){
        if(v !is String) return
        val a = v.split(" ").map{reV.num(it)?.let{it.toInt()} ?: it.toInt()}
        view.setPadding(a[3], a[0], a[1], a[2])//left,top,right,bottom
    }
}