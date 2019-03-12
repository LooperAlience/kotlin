package chela.tutorial.src1.viewmodel

import android.view.View
import chela.kotlin.DptoPx
import chela.tutorial.common.Holder
import chela.tutorial.common.SceneModel
import chela.tutorial.src1.App
import chela.tutorial.src1.holder.Sub
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM1: SceneModel(){
    var text = "Main Click!!"
    @PROP var fontSize = 15.0.DptoPx
    @PROP var click = View.OnClickListener {
        if(Holder.isLock) return@OnClickListener
        Holder.isLock = true
        App.router.push(Sub)
    }
}
