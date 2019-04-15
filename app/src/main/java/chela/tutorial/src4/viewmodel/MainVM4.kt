package chela.tutorial.src4.viewmodel

import chela.kotlin.Ch.DptoPx
import chela.tutorial.common.SceneModel
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM4: SceneModel(){
    @PROP val text = "Main"
    @PROP val fontSize = 15.0.DptoPx
}
