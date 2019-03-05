package chela.tutorial.viewmodel

import android.view.View
import chela.kotlin.DptoPx
import chela.kotlin.PxtoDp
import chela.kotlin.view.ChViewModel
import chela.tutorial.App
import chela.tutorial.holder.Main
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM:ChViewModel(){
    val holder = Holder()
    var userid = "loading"
    @PROP var fontSize = 15.0.DptoPx
    @PROP var visible = false

    override fun pushed() {
        holder.pushed()
        visible = true
    }
    override fun poped() {
        holder.poped()
        visible = false
    }
}
