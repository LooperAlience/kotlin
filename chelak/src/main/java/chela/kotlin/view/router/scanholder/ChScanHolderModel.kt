package chela.kotlin.view.router.scanholder

import chela.kotlin.Ch
import chela.kotlin.view.ChViewModel
import com.chela.annotation.VM

@VM
abstract class ChScanHolderModel:ChViewModel{
    constructor(
        push: Ch.WrapperType = Ch.WrapperType.RL,
        pop: Ch.WrapperType = Ch.WrapperType.LR,
        pushEase:String="circleOut",
        popEase:String="circleIn"
    ):super(){
        wrapper = Wrapper(push, pop, pushEase, popEase)
    }
    constructor(wrap:Wrapper):super(){
        wrapper = wrap
    }
    val wrapper:Wrapper
    fun wrapperPushAnimation(type:Ch.WrapperType, ease:String="circleOut"){
        wrapper.push = type
        wrapper.pushEase = ease
    }
    fun wrapperPopAnimation(type:Ch.WrapperType, ease:String="circleOut"){
        wrapper.pop = type
        wrapper.popEase = ease
    }
}