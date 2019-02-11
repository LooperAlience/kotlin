package chela.androidTest

import android.app.Application
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.model.Model

class User: Model(){
    var userid = ""
}
class App : Application(){
    override fun onCreate() {
        super.onCreate()

    }
}