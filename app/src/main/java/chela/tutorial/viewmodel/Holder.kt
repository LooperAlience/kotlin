package chela.tutorial.viewmodel

import android.view.View
import chela.kotlin.Ch
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import com.chela.annotation.EX
import com.chela.annotation.STYLE

@STYLE class Holder(
    @EX val pushX:Pair<Double, Double> = Ch.window.width.toDouble() to 0.0,
    @EX val pushA:Pair<Double, Double> = 0.0 to 1.0,
    @EX val popX:Pair<Double, Double> = 0.0 to -Ch.window.width.toDouble(),
    @EX val popA:Pair<Double, Double> =  1.0 to 0.0): ChStyleModel(){
    companion object {
        @EX val popTime = 1000
        @EX val pushTime = 350
    }
    var visibility = View.VISIBLE
    var x = pushX.first
    var alpha = pushA.first
    override fun pushed() {
        x = pushX.second
        alpha = pushA.second
    }
    override fun poped(){
        x = popX.second
        alpha = popA.second
    }
    override fun pushAnimation(it: ChItem) {
        x = it.circleOut(pushX.first, pushX.second)
        alpha = it.circleOut(pushA.first, pushA.second)
    }
    override fun popAnimation(it: ChItem) {
        x = it.circleOut(popX.first, popX.second)
        alpha = it.circleOut(popA.first, popA.second)
    }
}