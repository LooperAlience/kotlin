package chela.tutorial.src5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.src5.holder.Main
import kotlinx.android.synthetic.main.activity_container.*

class Act : AppCompatActivity(){
    companion object {
        val router = Ch.router(Ch.groupBase())
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        //(router.base as? ChGroupBase)?.group(main)
        router.base.group(main)
        App.looper.act(this)
        Ch.waitActivate(this, App.looper){ router.push(Main)}
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        Ch.permission.result(this, requestCode, permissions, grantResults)
    }
    override fun onBackPressed() {
        if(Act.router.pop() == 0){
            Act.router.clear()
            finish()
        }
    }
    override fun onStop() {
        Act.router.clear()
        super.onStop()
    }
}