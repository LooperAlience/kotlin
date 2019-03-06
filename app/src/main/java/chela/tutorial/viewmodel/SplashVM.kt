package chela.tutorial.viewmodel

import android.view.View
import chela.kotlin.DptoPx
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChViewModel
import chela.tutorial.App
import chela.tutorial.holder.Main
import com.chela.annotation.EX
import com.chela.annotation.PROP
import com.chela.annotation.STYLE
import com.chela.annotation.VM

@VM
object SplashVM:ChViewModel(){
    val holder = Holder()
    val title = Title

    @PROP var background = "#18ba9b"
    var isLock = true
    @PROP var click = View.OnClickListener {
        if(SplashVM.isLock) return@OnClickListener
        SplashVM.isLock = true
        App.router.push(Main)
    }
    override fun pushed() {
        holder.pushed()
        title.pushed()
    }
    override fun poped() {
        holder.poped()
        title.poped()
    }
}
@STYLE object Title:ChStyleModel(){
    @EX val time = 1000
    var alpha = 0.0
    var marginTop = 50.0.DptoPx
    var scaleX = 0.8
    var scaleY = 0.8
    override fun pushed(){
        alpha = 0.0
        marginTop = 50.0.DptoPx
        scaleX = 0.8
        scaleY = 0.8
    }
    override fun poped(){
        alpha = 1.0
        marginTop = 0.0
        scaleX = 1.0
        scaleY = 1.0
    }
    override fun pushAnimation(it:ChItem){
        alpha = it.circleOut(0.0, 1.0)
        marginTop = it.circleOut(50.0.DptoPx, 0.0)
        scaleX = it.circleOut(0.8, 1.0)
        scaleY = it.circleOut(0.8, 1.0)
    }
}