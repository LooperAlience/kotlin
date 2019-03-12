package chela.tutorial.src1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.src1.holder.Splash
import kotlinx.android.synthetic.main.activity_container.*

class Act : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        App.groupBase.group(main)
        App.looper.act(this)
        Ch.waitActivate(this, App.looper){ App.router.push(Splash)}
    }
    override fun onBackPressed() {
        //Todo alert..
        if(App.router.pop() == 0) finish()
    }
}