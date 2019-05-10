package chela.kotlin.view.router.scanholder

import chela.kotlin.view.ChViewModel

abstract class ChScanHolderModel(
    push: Wrapper.Type = Wrapper.Type.RL,
    pop: Wrapper.Type = Wrapper.Type.LR,
    pushEase:String="circleOut",
    popEase:String="circleIn"
):ChViewModel(){
    val wrapper = Wrapper(push, pop, pushEase, popEase)
}