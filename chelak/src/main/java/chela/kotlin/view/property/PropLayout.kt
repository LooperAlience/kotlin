package chela.kotlin.view.property

import android.util.Log
import android.view.View
import android.view.ViewGroup

object PropLayout:Property(){
    private inline fun param(view: View, block:(ViewGroup.LayoutParams)->ViewGroup.LayoutParams){
        view.layoutParams = block(view.layoutParams ?: ViewGroup.MarginLayoutParams(-1, -1))
    }
    fun width(view: View, v:Any){
        if(v !is Number) return
        param(view){
            it.width = v.toInt()
            it
        }
    }
    fun height(view: View, v:Any){
        if(v !is Number) return
        param(view){
            it.height = v.toInt()
            it
        }
    }
    fun margin(view: View, v:Any){
        param(view){
            val p:ViewGroup.MarginLayoutParams = it as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(it.width, it.height)
            @Suppress("UNCHECKED_CAST")
            if(v is String){
                val a = v.split(" ").map{it.toDouble().toInt()}
                p.setMargins(a[3], a[0], a[1], a[2])//left,top,right,bottom
            }else (v as? List<Int>)?.let{p.setMargins(it[3], it[0], it[1], it[2])}
            p
        }
    }
    fun marginStart(view: View, v:Any){
        if(v !is Number) return
        param(view){
            val p:ViewGroup.MarginLayoutParams = it as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(it.width, it.height)
            p.marginStart = v.toInt()
            p
        }
    }
    fun marginEnd(view: View, v:Any){
        if(v !is Number) return
        param(view){
            val p:ViewGroup.MarginLayoutParams = it as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(it.width, it.height)
            p.marginEnd = v.toInt()
            p
        }
    }
    fun marginTop(view: View, v:Any){
        if(v !is Number) return
        param(view){
            val p:ViewGroup.MarginLayoutParams = it as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(it.width, it.height)
            p.topMargin = v.toInt()
            p
        }
    }
    fun marginBottom(view: View, v:Any){
        if(v !is Number) return
        param(view){
            val p:ViewGroup.MarginLayoutParams = it as? ViewGroup.MarginLayoutParams ?: ViewGroup.MarginLayoutParams(it.width, it.height)
            p.bottomMargin = v.toInt()
            p
        }
    }
}