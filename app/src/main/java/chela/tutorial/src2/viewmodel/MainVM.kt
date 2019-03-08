package chela.tutorial.src2.viewmodel

import chela.kotlin.DptoPx
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object MainVM: SceneModel(){
    @PROP val text = "Main List"
    @PROP val fontSize = 15.0.DptoPx
}
