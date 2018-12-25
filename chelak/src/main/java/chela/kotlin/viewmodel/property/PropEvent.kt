package chela.kotlin.viewmodel.property

import android.os.Build
import android.view.View

object PropEvent:Property(){
    @JvmStatic fun click(view: View, v:Any){
        if(v !is View.OnClickListener) return
        view.isClickable = true
        view.setOnClickListener(v)
    }
    @JvmStatic fun clickable(view: View, v:Any){
        if(v !is Boolean) return
        view.isClickable = true
    }
    @JvmStatic fun longClickable(view: View, v:Any){
        if(v !is Boolean) return
        view.isLongClickable = true
    }
    @JvmStatic fun focusable(view:View, v:Any){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            view.focusable = when(v){
                is Boolean->if(v) View.FOCUSABLE else View.NOT_FOCUSABLE
                is Number-> v.toInt()
                else -> View.FOCUSABLE
            }
        }
    }
    @JvmStatic fun focusableInTouchMode(view:View, v:Any){
        if(v !is Boolean) return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            view.isFocusableInTouchMode = v
        }
    }
    @JvmStatic fun focus(view:View, v:Any){
        if(v !is Boolean) return
        focusable(view, v)
        if(v) view.requestFocus()
        focusableInTouchMode(view, v)
    }
}