package chela.androidTest.viewmodel

import android.view.View
import chela.androidTest.holder.Step1HD
import chela.androidTest.router
import chela.kotlin.model.Model

object MainVM: Model(){
    var width = 0.0
    var x = 0.0
    var visible = false

    var userid = "loading"
    var fontSize = 15.0
    val click = View.OnClickListener{router.push(Step1HD)}

    fun pushed(w:Double){
        width = w
        x = w
        visible = true
    }
    override fun set(k:String, v:Any):Boolean{
        if(k == "userid") userid = v.toString()
        return true
    }
}