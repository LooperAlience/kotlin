package chela.tutorial.src2

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.App
import chela.tutorial.src2.Act
import chela.tutorial.src2.holder.Main
import kotlinx.android.synthetic.main.activity_container.*

class Act : AppCompatActivity(){
    companion object {
        val groupBase = Ch.groupBase()
        val router = Ch.router(groupBase)
        var isPermitted = false
    }
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        groupBase.group(main)
        App.looper.act(this)
        Ch.waitActivate(this, App.looper){ router.push(Main)}

        with(Ch.permission(this, 15)){
            permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALENDAR,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ok{
                Log.i("ch", "all permitted")
                isPermitted = true
            }
            neverAsk{ Log.i("ch", "_never ask:$it")}
            denied{ p, res->
                Log.i("ch", "_denied:$p")
                isPermitted = false
                res.request()
            }
            request()
        }


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