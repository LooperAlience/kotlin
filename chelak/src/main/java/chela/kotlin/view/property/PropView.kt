package chela.kotlin.view.property

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import chela.kotlin.android.ChApp
import chela.kotlin.view.ChDrawable

object PropView:Property(){
    @JvmStatic fun tag(view:View, v:Any){
        if(v !is String) return
        view.tag = v
    }
    @JvmStatic fun isEnabled(view:View, v:Any){
        if(v !is Boolean) return
        view.isEnabled = v
    }
    @JvmStatic fun visibility(view:View, v:Any){
        view.visibility = when(v){
            is Boolean->if(v) View.VISIBLE else View.GONE
            is Number-> v.toInt()
            else -> View.VISIBLE
        }
    }
    @JvmStatic fun background(view:View, v:Any){
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
    @JvmStatic fun shadow(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.elevation = v.toFloat()
    }
    @JvmStatic fun x(view:View, v:Any){
        if(v !is Number) return
        view.translationX = v.toFloat()
    }
    @JvmStatic fun y(view:View, v:Any){
        if(v !is Number) return
        view.translationY = v.toFloat()
    }
    @JvmStatic fun z(view:View, v:Any){
        if(v !is Number || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return
        view.translationZ = v.toFloat()
    }
    @JvmStatic fun scaleX(view:View, v:Any){
        if(v !is Number) return
        view.scaleX = v.toFloat()
    }
    @JvmStatic fun scaleY(view:View, v:Any){
        if(v !is Number) return
        view.scaleY = v.toFloat()
    }
    @JvmStatic fun rotation(view:View, v:Any){
        if(v !is Number) return
        view.rotation = v.toFloat()
    }
    @JvmStatic fun alpha(view:View, v:Any){
        if(v !is Number) return
        view.alpha = v.toFloat()
    }
    @JvmStatic fun paddingStart(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(v.toInt(), view.paddingTop, view.paddingEnd, view.paddingBottom)
    }
    @JvmStatic fun paddingEnd(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, view.paddingTop, v.toInt(), view.paddingBottom)
    }
    @JvmStatic fun paddingTop(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, v.toInt(), view.paddingEnd, view.paddingBottom)
    }
    @JvmStatic fun paddingBottom(view:View, v:Any){
        if(v !is Number) return
        view.setPadding(view.paddingStart, view.paddingTop, view.paddingEnd, v.toInt())
    }
    @JvmStatic fun padding(view:View, v:Any){
        if(v !is String) return
        val a = v.split(" ").map{it.toInt()}
        view.setPadding(a[3], a[0], a[1], a[2])//left,top,right,bottom
    }
}