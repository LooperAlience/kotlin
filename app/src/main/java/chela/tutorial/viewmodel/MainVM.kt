package chela.tutorial.viewmodel

import android.util.Log
import android.view.View
import chela.kotlin.DptoPx
import chela.kotlin.PxtoDp
import chela.kotlin.view.ChViewModel
import chela.tutorial.App
import chela.tutorial.holder.Main
import chela.tutorial.holder.Sub
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM:ChViewModel(){
    val holder = Holder()
    var text = "Main CLICK!!!"
    @PROP var fontSize = 15.0.DptoPx
    @PROP var visible = false
    var isLock = true
    @PROP var click = View.OnClickListener {
        Log.i("ch", "click")
        if(isLock) return@OnClickListener

        Log.i("ch", "isLock ${isLock}")
        isLock = true
        App.router.push(Sub)
    }
    override fun pushed() {
        Log.i("ch", "pushed")
        holder.pushed()
        visible = true
    }
    override fun poped() {
        holder.poped()
        visible = false
    }
}
