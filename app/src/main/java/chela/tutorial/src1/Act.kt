package chela.tutorial.src1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.src1.holder.Splash
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
        Ch.waitActivate(this, App.looper){ router.push(Splash)}
    }
    override fun onBackPressed() {
        if(router.pop() == 0){
            router.clear()
            finish()
        }
    }
    override fun onStop() {
        router.clear()
        super.onStop()
    }
}