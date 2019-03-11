package chela.tutorial.src3.viewmodel

import chela.kotlin.DptoPx
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM: SceneModel(){
    @PROP val text = "Main"
    @PROP val fontSize = 15.0.DptoPx
}
