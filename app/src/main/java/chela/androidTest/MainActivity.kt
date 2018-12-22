package chela.androidTest

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import chela.androidTest.holder.MainHD
import chela.kotlin.Ch
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.looper.ChLooper
import chela.kotlin.looper.ChLooper.Item.Time
import chela.kotlin.viewmodel.ChRouter
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("StaticFieldLeak")
var groupBase = Ch.groupBase()
val router = Ch.router(groupBase)
var looper = Ch.looper()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        groupBase.group(base)
        looper.act(this)
        looper.add(Time(1000)){
            if(base.width > 0){
                router.push(MainHD, false)
                it.stop()
            }
        }
    }
}