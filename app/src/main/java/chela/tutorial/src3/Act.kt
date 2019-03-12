package chela.tutorial.src3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.src3.Act
import chela.tutorial.src3.holder.Main
import kotlinx.android.synthetic.main.activity_container.*

class Act : AppCompatActivity(){
    companion object {
        val groupBase = Ch.groupBase()
        val router = Ch.router(groupBase)
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        groupBase.group(main)
        App.looper.act(this)
        Ch.waitActivate(this, App.looper){ router.push(Main)}
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