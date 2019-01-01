package chela.androidTest.viewmodel

import android.view.MotionEvent
import chela.kotlin.Ch
import chela.kotlin.model.Model

object HikaVM: Model(){
    var title = "touch test"
    var fontSize = 15.0
    val move = object:Ch.Touch{
        override fun onTouch(e:MotionEvent):Boolean{
            val x = e.getX(0)
            val y = e.getY(0)
            val p = y / Ch.app.height
            fontSize = 15.0 + 50.0 * p
            Ch.scanner["hika"]?.renderSync()
            return true
        }
    }
}