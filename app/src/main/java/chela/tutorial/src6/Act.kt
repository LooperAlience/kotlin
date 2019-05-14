package chela.tutorial.src6

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.kotlin.view.router.scanholder.ChScanHolder
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.src5.Act
import kotlinx.android.synthetic.main.activity_container.*

class Act : AppCompatActivity(){
    companion object {
        val router = Ch.router(Ch.groupBase())
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Ch.debugLevel = 1
        setContentView(R.layout.activity_container)
        router.base.group(main)
        Ch.waitActivate(this, Ch.groupLooper){
            if(!router.restore()){
                router.push(Holder("1", R.layout.activity_main6, MainVm("test1", "#ff0000")))
                router.push(Holder("2", R.layout.activity_main6, MainVm("test2", "#ffff00")))
                router.push(Holder("3", R.layout.activity_main6, MainVm("test3", "#ff00ff")))
                router.push(Holder("4", R.layout.activity_main6, MainVm("test4", "#0000ff")))
                router.push(Holder("5", R.layout.activity_main6, MainVm("test5", "#00ff00")))
            }
        }
    }
}