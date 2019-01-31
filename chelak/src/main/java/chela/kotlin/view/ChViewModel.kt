package chela.kotlin.view

import chela.kotlin.looper.ChItem
import chela.kotlin.model.Model
import com.chela.annotation.EX
import com.chela.annotation.STYLE

/**
 * This class represent viewmodel lifecycle event.
 */
abstract class ChViewModel: Model(){
    open fun start(){}
    open fun end(){}
    open fun paused(){}
    open fun resumeAnimation(it: ChItem){}
    open fun pauseAnimation(it: ChItem){}
}
/**
 * This class represent viewmodel UI style.
 * Exclude annotation helps you remove that style in the list.
 */
abstract class ChStyleModel(@EX val isRegister:Boolean = false): ChViewModel()