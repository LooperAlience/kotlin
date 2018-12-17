package chela.test.viewmodel

import chela.kotlin.viewmodel.ChViewModel


object Step1VM: ChViewModel(){
    var userid = ""
    override fun set(k: String, v: Any):Boolean{
        if(userid.isNotEmpty()) return false
        if(k == "userid") userid = v.toString()
        return true
    }
}