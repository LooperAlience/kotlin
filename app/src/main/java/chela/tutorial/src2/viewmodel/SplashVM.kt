package chela.tutorial.src2.viewmodel

import android.util.Log
import android.view.View
import android.widget.Toast
import chela.kotlin.Ch
import chela.kotlin.DptoPx
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import chela.tutorial.src2.App
import chela.tutorial.src2.holder.Main
import com.chela.annotation.EX
import com.chela.annotation.PROP
import com.chela.annotation.STYLE
import com.chela.annotation.VM

@VM
object SplashVM: SceneModel(0){
    val title = Title
    @PROP var background = "#18ba9b"
    @PROP var click = View.OnClickListener {
        Log.i("ch", "click${App.isPermitted}")
        if(Holder.isLock) return@OnClickListener
        Holder.isLock = true
        if(App.isPermitted) App.router.push(Main) else Toast.makeText(Ch.app.app, "퍼미션 동의를 해야 다음으로 넘어갑니다", Toast.LENGTH_SHORT).show()
    }
}

@VM
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