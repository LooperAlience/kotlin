package chela.kotlin.view

import chela.kotlin.looper.ChItem
import chela.kotlin.model.Model

/**
 * This class represent viewmodel lifecycle event.
 */
abstract class ChViewModel: Model(){
    @Target(AnnotationTarget.PROPERTY) annotation class Prop(val name:String = "")
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
abstract class ChStyleModel(@Exclude val isRegister:Boolean = false): ChViewModel(){
    @Target(AnnotationTarget.PROPERTY) annotation class Exclude
}