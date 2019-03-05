package chela.tutorial.viewmodel

import android.view.View
import chela.kotlin.Ch
import chela.kotlin.looper.ChItem
import chela.kotlin.view.ChStyleModel
import com.chela.annotation.EX
import com.chela.annotation.STYLE

@STYLE class Holder(
    @EX val resumeX:Pair<Double, Double> = Ch.window.width.toDouble() to 0.0,
    @EX val resumeA:Pair<Double, Double> = 0.0 to 1.0,
    @EX val pauseX:Pair<Double, Double> = 0.0 to -Ch.window.width.toDouble(),
    @EX val pauseA:Pair<Double, Double> =  1.0 to 0.0): ChStyleModel(){
    companion object {
        @EX val popTime = 320
        @EX val pushTime = 350
    }
    var visibility = View.VISIBLE
    var x = resumeX.first
    var alpha = resumeA.first
    override fun resumed() {
        x = resumeX.second
        alpha = resumeA.second
        visibility = View.VISIBLE
    }
    override fun paused(){
        x = pauseX.second
        alpha = pauseA.second
        visibility = View.GONE
    }
    override fun resumeAnimation(it: ChItem) {
        x = it.circleOut(resumeX.first, resumeX.second)
        alpha = it.circleOut(resumeA.first, resumeA.second)
    }
    override fun pauseAnimation(it: ChItem) {
        //x = it.circleOut(pauseX.first, pauseX.second)
        //alpha = it.circleOut(pauseA.first, pauseA.second)
    }
}