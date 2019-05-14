package chela.kotlin.view.router.scanholder

import chela.kotlin.Ch
import chela.kotlin.view.ChViewModel

abstract class ChScanHolderModel(
    push: Ch.WrapperType = Ch.WrapperType.RL,
    pop: Ch.WrapperType = Ch.WrapperType.LR,
    pushEase:String="circleOut",
    popEase:String="circleIn"
):ChViewModel(){
    val wrapper = Wrapper(push, pop, pushEase, popEase)
    fun wrapperPushAnimation(type:Ch.WrapperType, ease:String="circleOut"){
        wrapper.push = type
        wrapper.pushEase = ease
    }
    fun wrapperPopAnimation(type:Ch.WrapperType, ease:String="circleOut"){
        wrapper.pop = type
        wrapper.popEase = ease
    }
}