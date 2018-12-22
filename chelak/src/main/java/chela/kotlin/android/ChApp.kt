package chela.kotlin.android

import android.app.Application
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.DisplayMetrics

object ChApp{
    @JvmStatic lateinit var app: Application
    @JvmStatic lateinit var res: Resources
    @JvmStatic lateinit var asset: AssetManager
    @JvmStatic lateinit var shared: SharedPreferences
    @JvmStatic lateinit var packName:String
    @JvmStatic var toPx = 0.0
    @JvmStatic var toDp = 0.0
    @JvmStatic fun app(a: Application){
        app = a
        res = a.resources
        asset = a.assets
        packName = a.packageName
        val m = res.displayMetrics.densityDpi.toDouble()
        toPx = m / DisplayMetrics.DENSITY_DEFAULT
        toDp = DisplayMetrics.DENSITY_DEFAULT / m
    }
    @JvmStatic fun resS2I(type:String, name:String):Int = res.getIdentifier(name, type, packName)
    @JvmStatic fun resDrawable(v: String):Int = resS2I("drawable", v)
    @JvmStatic fun resId(v: String):Int = resS2I("id", v)
    @JvmStatic fun resLayout(v: String):Int = resS2I("layout", v)
    @JvmStatic fun resName(id:Int):String = res.getResourceEntryName(id)
}