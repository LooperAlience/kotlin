package chela.tutorial

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.holder.Splash
import kotlinx.android.synthetic.main.appmain.*

class Act : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appmain)
        App.groupBase.group(main)
        App.looper.act(this)
        Ch.waitActivate(this, App.looper){App.router.push(Splash)}
    }
    override fun onConfigurationChanged(newConfig: Configuration){
        super.onConfigurationChanged(newConfig)
    }
}