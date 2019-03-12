package chela.tutorial.src1.viewmodel

import android.view.View
import chela.tutorial.common.Holder
import chela.tutorial.common.SceneModel
import chela.tutorial.src1.App
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object SubVM: SceneModel(){
    val list = listOf("aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc")
    var text = "뒤로가기"
    @PROP var click = View.OnClickListener {
        if(Holder.isLock) return@OnClickListener
        Holder.isLock = true
        App.router.pop()
    }
}
