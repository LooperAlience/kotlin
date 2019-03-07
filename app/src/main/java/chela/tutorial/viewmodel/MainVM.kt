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
object MainVM:SceneModel(){
    var text = "Main CLICK!!!"
    @PROP var fontSize = 15.0.DptoPx
    @PROP var click = View.OnClickListener {
        if(Holder.isLock) return@OnClickListener
        Holder.isLock = true
        App.router.push(Sub)
    }
}
