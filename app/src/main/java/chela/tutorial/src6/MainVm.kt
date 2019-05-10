package chela.tutorial.src6

import android.util.Log
import android.view.View
import chela.kotlin.Ch
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.router.scanholder.ChScanHolderModel
import chela.kotlin.view.router.scanholder.Wrapper
import com.chela.annotation.STYLE

class MainVm(text:String, bg:String) : ChScanHolderModel(Wrapper.Type.NO, Wrapper.Type.NO) {
    val text = Text(text, bg)
}
@STYLE
class Text(val text:String, val background:String):ChStyleModel(){
    val click = Ch.click{
        Act.router.pop()
    }
}