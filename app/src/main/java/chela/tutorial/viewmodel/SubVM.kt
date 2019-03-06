package chela.tutorial.viewmodel

import android.util.Log
import android.view.View
import chela.kotlin.DptoPx
import chela.kotlin.view.ChViewModel
import chela.tutorial.App
import chela.tutorial.holder.Sub
import com.chela.annotation.PROP
import com.chela.annotation.VM

@VM
object SubVM:ChViewModel(){
    val holder = Holder()
    val list = listOf("aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc","aaa", "bbb", "ccc")
    var text = "뒤로가기"
    var isLock = true
    @PROP var click = View.OnClickListener {
        if(isLock) return@OnClickListener
        isLock = true
        App.router.pop()
    }
    override fun pushed() {
        Log.i("ch", "sub pushed")
        holder.pushed()
        MainVM.isLock = false
    }
    override fun poped() {
        holder.poped()
        isLock = false
    }
}
