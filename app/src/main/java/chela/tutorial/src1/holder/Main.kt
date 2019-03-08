package chela.tutorial.src1.holder

import chela.tutorial.R
import chela.tutorial.src1.viewmodel.MainVM


object Main : Scene() {
    override fun vm() = MainVM
    override fun layout() = R.layout.activity_main
    override fun init(){}
    override fun pushed(){}
}