package chela.kotlin.view.property

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import chela.kotlin.Ch
import chela.kotlin.android.ChApp

object PropSwitch:Property(){
    fun ison(view: View, v:Any){
        if(view !is SwitchCompat || v !is Boolean) return
        view.isChecked = v
    }
    fun track(view: View, v:Any){
        if(view !is SwitchCompat) return
        when(v){
            is String->view.trackDrawable = ChApp.drawable(v)
            is Drawable ->view.trackDrawable = v
            is Int->view.setTrackResource(v)
            else->throw Throwable("Invalid track resource : $v")
        }
    }
    fun thumb(view: View, v:Any){
        if(view !is SwitchCompat) return
        when(v){
            is String->view.thumbDrawable = ChApp.drawable(v)
            is Drawable ->view.thumbDrawable = v
            is Int->view.setThumbResource(v)
            else->throw Throwable("Invalid thumb resource : $v")
        }
    }
    fun thumbpadding(view: View, v:Any){
        if(view !is SwitchCompat || v !is Int) return
        view.thumbTextPadding = v
    }
    fun textoff(view: View, v:Any){
        if(view !is SwitchCompat) return
        view.textOff = "$v"
    }
    fun texton(view: View, v:Any){
        if(view !is SwitchCompat) return
        view.textOn = "$v"
    }
    fun switchminwidth(view: View, v:Any){
        if(view !is SwitchCompat || v !is Int) return
        view.switchMinWidth = v
    }
}
