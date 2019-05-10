package chela.tutorial.src5.viewmodel

import android.view.View
import chela.kotlin.view.ChStyleModel
import chela.tutorial.common.SceneModel
import chela.tutorial.src5.holder.Main
import com.chela.annotation.PROP
import com.chela.annotation.STYLE
import com.chela.annotation.VM

@VM
object MainVM5: SceneModel(){
    val text = Txt
    @PROP val text2 = "@{cdata.test2}"
    @PROP val text3 = "@{cdata.test3}"
    val btn = Btn
}
@STYLE
object Txt:ChStyleModel(){
    val text = "@{cdata.test}"
}
@STYLE
object Btn:ChStyleModel(){
    val text = "rerender"
    val click = View.OnClickListener {
        Main.click()
    }
}