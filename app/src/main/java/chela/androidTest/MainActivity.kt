package chela.androidTest

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chela.androidTest.holder.MainFHD
import chela.androidTest.holder.MainHD
import chela.kotlin.Ch
import chela.kotlin.looper.ChLooper.Item.Time
import kotlinx.android.synthetic.main.activity_main.*

@SuppressLint("StaticFieldLeak")
var groupBase = Ch.groupBase()
val router = Ch.router(groupBase)

val framentBase = Ch.fragmentBase()
val routerf = Ch.router(framentBase)

var looper = Ch.looper()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        groupBase.group(_group)
        framentBase.manager = supportFragmentManager
        framentBase.container = _fragment.id
        with(looper) {
            act(this@MainActivity)
            add(Time(1000)) {
                if (_group.width == 0) return@add
                router.push(MainHD, false)
                routerf.push(MainFHD)
                it.stop()
            }
        }
        with(Ch.permission(this, 15)){
            permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALENDAR
            )
            ok{Log.i("ch", "all permitted")}
            neverAsk{Log.i("ch", "_never ask:$it")}
            denied{ p, res->
                Log.i("ch", "_denied:$p")
                res.request()
            }
            request()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        Ch.permission.result(this, requestCode, permissions, grantResults)
    }
    override fun onBackPressed() {
        if(router.pop() == 0) Ch.finish(this)
    }
}