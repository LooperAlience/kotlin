package chela.kotlin.viewmodel.property

import android.view.View
import android.widget.TextView

object PropText:Property(){
    @JvmStatic fun text(view:View, v:Any){
        if(view !is TextView) return
        view.text = v as String
    }
    @JvmStatic fun textSize(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textSize = v.toFloat()
    }
    @JvmStatic fun textScaleX(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.textScaleX = v.toFloat()
    }
    @JvmStatic fun lineSpacing(view:View, v:Any){
        if(v !is Number || view !is TextView) return
        view.setLineSpacing(v.toFloat(), 1F)
    }
    @JvmStatic fun textColor(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.setTextColor(ChProperty.color(v))
    }
    @JvmStatic fun hint(view:View, v:Any){
        if(v !is String || view !is TextView) return
        view.hint = v
    }
}