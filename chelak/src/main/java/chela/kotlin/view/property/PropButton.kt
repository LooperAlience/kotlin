package chela.kotlin.view.property

import android.view.View
import android.widget.Button
import chela.kotlin.Ch

object PropButton:Property(){
    private val rex = """^(left|right|top|bottom)@(.+)$""".toRegex()
    fun drawable(view: View, v:Any){
        if(view !is Button) return
        when(v){
            is Ch.ButtonDrawable -> view.setCompoundDrawablesWithIntrinsicBounds(v.left, v.top, v.right, v.bottom)
            is String ->{
                if(rex.matches(v)){
                    rex.matchEntire(v)?.let{
                        val r = it.groupValues[2]
                        when(it.groupValues[1].toLowerCase()){
                            "left"->view.setCompoundDrawablesWithIntrinsicBounds(Ch.ButtonDrawable.d(r), null, null, null)
                            "top"->view.setCompoundDrawablesWithIntrinsicBounds(null, Ch.ButtonDrawable.d(r), null, null)
                            "right"->view.setCompoundDrawablesWithIntrinsicBounds(null, null, Ch.ButtonDrawable.d(r), null)
                            "bottom"->view.setCompoundDrawablesWithIntrinsicBounds(null, null, null, Ch.ButtonDrawable.d(r))
                        }
                    }
                }
            }
        }
    }
}
