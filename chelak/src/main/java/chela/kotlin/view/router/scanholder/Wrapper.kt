package chela.kotlin.view.router.scanholder

import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChWindow
import com.chela.annotation.EX
import com.chela.annotation.STYLE

@STYLE
class Wrapper(
    @EX internal val push:Type,
    @EX internal val pop:Type,
    @EX internal val pushEase:String,
    @EX internal val popEase:String
):ChStyleModel(){
    companion object {
        @EX internal val popTime = 320L
        @EX internal val pushTime = 320L
    }
    sealed class Type(val isX:Boolean){
        object LR:Type(true)
        object RL:Type(true)
        object TB:Type(false)
        object BT:Type(false)
        object NO:Type(false)
    }
    @EX val pushed = when(push){
        Type.LR-> -ChWindow.width.toDouble() to 0.0
        Type.RL-> ChWindow.width.toDouble() to 0.0
        Type.TB-> -ChWindow.height.toDouble() to 0.0
        Type.BT-> ChWindow.height.toDouble() to 0.0
        Type.NO-> 0.0 to 0.0
    }
    @EX val poped = when(pop){
        Type.LR-> 0.0 to ChWindow.width.toDouble()
        Type.RL-> 0.0 to -ChWindow.width.toDouble()
        Type.TB-> 0.0 to ChWindow.height.toDouble()
        Type.BT-> 0.0 to -ChWindow.height.toDouble()
        Type.NO-> 0.0 to 0.0
    }

    var x = 0.0
    var y = 0.0
    var clickable = true
    var visible = true

    override fun pushed() {
        if(push.isX) x = pushed.second else y = pushed.second
        visible = true
    }
    override fun poped(){
        if(pop.isX) x = poped.second else y = poped.second
        visible = false
    }
    override fun pushAnimation(it: ChItem){
        val v = it.ease(pushEase, pushed.first, pushed.second)
        if (push.isX) x = v else y = v
    }
    override fun popAnimation(it: ChItem){
        val v = it.ease(popEase, poped.first, poped.second)
        if (pop.isX) x = v else y = v
    }
}