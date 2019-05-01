package chela.tutorial.src4.holder

import chela.tutorial.R
import chela.tutorial.common.Scene
import chela.tutorial.src5.viewmodel.MainVM5


object Main : Scene() {
    override fun vm() = MainVM5
    override fun layout() = R.layout.activity_main4
    override fun init(){
    }
    override fun pushed(){}
}


