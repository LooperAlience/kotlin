package chela.kotlin.android

import android.content.pm.ActivityInfo.*
import android.util.Log
import android.view.View
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch

object ChWindow{
    @JvmStatic var toPx = 0.0
    @JvmStatic var toDp = 0.0
    @JvmStatic val width:Int get() = ChApp.dm.widthPixels
    @JvmStatic val height:Int get() = ChApp.dm.heightPixels

    @JvmStatic fun topOffset(view:View) = ChView.getActivity(view)?.let{topOffset(it)} ?: 0
    @JvmStatic fun topOffset(act:AppCompatActivity) = height - act.findViewById<View>(android.R.id.content).measuredHeight

    @JvmStatic fun fullOn(act: AppCompatActivity) = act.window.addFlags(FLAG_FULLSCREEN)
    @JvmStatic fun fullOff(act: AppCompatActivity) = act.window.clearFlags(FLAG_FULLSCREEN)
    @JvmStatic fun screenOn(act: AppCompatActivity) = act.window.addFlags(FLAG_KEEP_SCREEN_ON)
    @JvmStatic fun screenOff(act: AppCompatActivity) = act.window.clearFlags(FLAG_KEEP_SCREEN_ON)
    @JvmStatic fun noTitleBar(act: AppCompatActivity) = act.requestWindowFeature(FEATURE_NO_TITLE)
    @JvmStatic fun landscape(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_LANDSCAPE}
    @JvmStatic fun landscapeSensor(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_SENSOR_LANDSCAPE}
    @JvmStatic fun landscapeReverse(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_REVERSE_LANDSCAPE}
    @JvmStatic fun portrait(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_PORTRAIT}
    @JvmStatic fun portraitSensor(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_SENSOR_PORTRAIT}
    @JvmStatic fun portraitReverse(act: AppCompatActivity){act.requestedOrientation = SCREEN_ORIENTATION_REVERSE_PORTRAIT}
    @JvmStatic fun toast(msg:String, isLong:Boolean = false) = Toast.makeText(ChApp.app, msg, if(isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()

}