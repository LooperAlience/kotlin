package chela.tutorial.src6

import chela.kotlin.Ch
import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.router.scanholder.ChScanHolderModel
import chela.kotlin.view.router.scanholder.Wrapper
import com.chela.annotation.STYLE

class MainVm(text:String, bg:String) : ChScanHolderModel(Wrapper.WrapperType.NO, Wrapper.WrapperType.NO) {
    val text = Text(text, bg)
}
@STYLE
class Text(val text:String, val background:String):ChStyleModel(){
    val click = Ch.click{
        Act.router.pop()
    }
}