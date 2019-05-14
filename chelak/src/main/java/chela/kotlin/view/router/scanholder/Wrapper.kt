package chela.kotlin.view.router.scanholder

import android.util.Log
import chela.kotlin.Ch.WrapperType
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChWindow
import com.chela.annotation.EX
import com.chela.annotation.STYLE

@STYLE
class Wrapper(
    @EX internal var push:WrapperType,
    @EX internal var pop:WrapperType,
    @EX internal var pushEase:String,
    @EX internal var popEase:String
):ChStyleModel(){
    companion object {
        @EX internal var popTime = 320L
        @EX internal var pushTime = 320L
    }

    @EX val pushed = when(push){
        WrapperType.LR-> -ChWindow.width.toDouble() to 0.0
        WrapperType.RL-> ChWindow.width.toDouble() to 0.0
        WrapperType.TB-> -ChWindow.height.toDouble() to 0.0
        WrapperType.BT-> ChWindow.height.toDouble() to 0.0
        WrapperType.NO-> 0.0 to 0.0
    }
    @EX val poped = when(pop){
        WrapperType.LR-> 0.0 to ChWindow.width.toDouble()
        WrapperType.RL-> 0.0 to -ChWindow.width.toDouble()
        WrapperType.TB-> 0.0 to ChWindow.height.toDouble()
        WrapperType.BT-> 0.0 to -ChWindow.height.toDouble()
        WrapperType.NO-> 0.0 to 0.0
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