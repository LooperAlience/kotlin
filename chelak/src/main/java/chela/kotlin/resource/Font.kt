package chela.kotlin.resource

import android.util.Log
import chela.kotlin.view.ChStyle

class Font(private val font:String){
    fun set(k:String) = ChStyle.addFont(k, font)
    fun remove(k:String) = ChStyle.removeFont(k)
}