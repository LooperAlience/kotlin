package chela.androidTest

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import chela.androidTest.viewmodel.HikaVM
import chela.kotlin.Ch
import kotlinx.android.synthetic.main.hika.*

class Hika:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hika)
        Log.i("ch", HikaVM.title)
        Ch.prop.view.background(root, "#0000ff")
        Ch.scanner.scan("hika", root).render()
    }
}