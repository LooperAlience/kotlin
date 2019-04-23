package chela.kotlin.view

import chela.kotlin.looper.ChItem
import chela.kotlin.model.Model
import com.chela.annotation.EX
import com.chela.annotation.STYLE

abstract class ChViewModel(isRegister:Boolean = true, name:String? = null):Model(isRegister, name){
    open fun resumed(){}
    open fun resumeAnimation(it: ChItem){}
    open fun paused(){}
    open fun pauseAnimation(it: ChItem){}
    open fun pushed(){}
    open fun pushAnimation(it: ChItem){}
    open fun poped(){}
    open fun popAnimation(it: ChItem){}
}
abstract class ChStyleModel(isRegister:Boolean = false, name:String? = null): ChViewModel(isRegister, name)