package chela.tutorial.src3.viewmodel

import chela.kotlin.Ch.DptoPx
import chela.tutorial.common.SceneModel
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM3: SceneModel(1){
    @PROP val text = "Main"
    @PROP val fontSize = 15.0.DptoPx
}
