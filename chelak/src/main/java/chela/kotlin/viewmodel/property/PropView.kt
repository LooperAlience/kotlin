package chela.kotlin.viewmodel.property

import android.os.Build
import android.view.View

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
        if(v !is String) return
        view.setBackgroundColor(ChProperty.color(v))
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
    @JvmStatic fun alpha(view:View, v:Any){
        if(v !is Number) return
        view.alpha = v.toFloat()
    }
    @JvmStatic fun padding(view:View, v:Any){
        if(v !is String) return
        val a = v.split(" ").map{it.toInt()}
        view.setPadding(a[3], a[0], a[1], a[2])//left,top,right,bottom
    }
}