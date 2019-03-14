package chela.tutorial.src1.holder

import chela.tutorial.R
import chela.tutorial.common.Scene
import chela.tutorial.src1.viewmodel.MainVM1
import chela.tutorial.src1.Act


object Main : Scene() {
    override fun vm() = MainVM1
    override fun layout() = R.layout.activity_main1
    override fun init(){}
    override fun pushed(){
        //Act.router.take(0)
    }
}