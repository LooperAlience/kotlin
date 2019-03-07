package chela.tutorial.holder

import chela.tutorial.R
import chela.tutorial.viewmodel.MainVM


object Main : Scene() {
    override fun vm() = MainVM
    override fun layout() = R.layout.activity_main
    override fun init(){}
    override fun pushed(){}
}