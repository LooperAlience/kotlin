package chela.kotlin.view

import chela.kotlin.looper.ChItem
import chela.kotlin.model.Model
import com.chela.annotation.EX
import com.chela.annotation.STYLE

/**
 * This class represent viewmodel lifecycle event.
 */
abstract class ChViewModel: Model(){
    open fun resumed(){}
    open fun resumeAnimation(it: ChItem){}
    open fun paused(){}
    open fun pauseAnimation(it: ChItem){}
    open fun pushed(){}
    open fun pushAnimation(it: ChItem){}
    open fun poped(){}
    open fun popAnimation(it: ChItem){}
}
/**
 * This class represent viewmodel UI style.
 * Exclude annotation helps you remove that style in the list.
 */
abstract class ChStyleModel(@EX val isRegister:Boolean = false): ChViewModel()