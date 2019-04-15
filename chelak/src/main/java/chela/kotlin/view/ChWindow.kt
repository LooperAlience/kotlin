package chela.kotlin.view

import android.content.pm.ActivityInfo.*
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.android.ChApp

object ChWindow{


    var DptoPx = 0.0
    var PxtoDp = 0.0
    var PxtoSp = 0.0
    var SptoPx = 0.0
    val width:Int get() = ChApp.dm.widthPixels
    val height:Int get() = ChApp.dm.heightPixels

    fun topOffset(view:View) = ChView.getActivity(view)?.let{ topOffset(it) } ?: 0
    fun topOffset(act:AppCompatActivity) = height - act.findViewById<View>(android.R.id.content).measuredHeight

    fun fullOn(act: AppCompatActivity) = act.window.addFlags(FLAG_FULLSCREEN)
    fun fullOff(act: AppCompatActivity) = act.window.clearFlags(FLAG_FULLSCREEN)
    fun screenOn(act: AppCompatActivity) = act.window.addFlags(FLAG_KEEP_SCREEN_ON)
    fun screenOff(act: AppCompatActivity) = act.window.clearFlags(FLAG_KEEP_SCREEN_ON)
    fun noTitleBar(act: AppCompatActivity) = act.requestWindowFeature(FEATURE_NO_TITLE)
    fun landscape(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE}
    fun landscapeSensor(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE}
    fun landscapeReverse(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_REVERSE_LANDSCAPE}
    fun portrait(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT}
    fun portraitSensor(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_SENSOR_PORTRAIT}
    fun portraitReverse(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_REVERSE_PORTRAIT}
    fun toast(msg:String, isLong:Boolean = false) = Toast.makeText(ChApp.app, msg, if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

}