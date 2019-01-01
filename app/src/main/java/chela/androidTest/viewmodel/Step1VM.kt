package chela.test.viewmodel

import chela.kotlin.model.Model


object Step1VM: Model(){
    var userid = ""
    override fun set(k: String, v: Any):Boolean{
        if(userid.isNotEmpty()) return false
        if(k == "userid") userid = v.toString()
        return true
    }
}