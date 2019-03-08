package chela.tutorial.src2.viewmodel

import chela.kotlin.Ch
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChViewModel
import com.chela.annotation.EX
import com.chela.annotation.STYLE


abstract class SceneModel(holderType:Int = 0): ChViewModel(){
    lateinit var holder: Holder
    init{
        when(holderType){
            0 -> holder = Holder()
            1 -> holder = Holder(0.0 to 0.0)
        }
    }
    override fun pushAnimation(it: ChItem){
        holder.pushAnimation(it)
    }
    override fun popAnimation(it: ChItem) {
        holder.popAnimation(it)
    }
    override fun pushed() {
        holder.pushed()
        Holder.isLock = false
    }
    override fun poped() {
        holder.poped()
        Holder.isLock = false
    }
}


@STYLE
class Holder(
    @EX val pushX:Pair<Double, Double> = Ch.window.width.toDouble() to 0.0,
    @EX val popX:Pair<Double, Double> = 0.0 to Ch.window.width.toDouble()): ChStyleModel(){
    companion object {
        @EX val popTime = 320
        @EX val pushTime = 350
        @EX var isLock = false
    }
    var x = pushX.first
    var clickable = true
    var visible = true
    override fun pushed() {
        x = pushX.second
        visible = true
    }
    override fun poped(){
        x = popX.second
        visible = false
    }
    override fun pushAnimation(it: ChItem) {
        x = it.circleOut(pushX.first, pushX.second)
    }
    override fun popAnimation(it: ChItem) {
        x = it.circleIn(popX.first, popX.second)
    }
}