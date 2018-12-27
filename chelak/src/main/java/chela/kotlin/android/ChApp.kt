package chela.kotlin.android

import android.app.Application
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager

/**
 * Base object for accessing an application's resource and converting display unit.
 */
object ChApp{
    @JvmStatic lateinit var app: Application
    @JvmStatic lateinit var res: Resources
    @JvmStatic lateinit var asset: AssetManager
    @JvmStatic lateinit var imm: InputMethodManager
    @JvmStatic lateinit var packName:String
    @JvmStatic lateinit var dm: DisplayMetrics
    @JvmStatic val width:Int get() = dm.widthPixels
    @JvmStatic val height:Int get() = dm.heightPixels
    @JvmStatic var toPx = 0.0
    @JvmStatic var toDp = 0.0
    @JvmStatic fun app(a: Application){
        app = a
        res = a.resources
        asset = a.assets
        packName = a.packageName
        dm = res.displayMetrics
        val m = dm.densityDpi.toDouble()
        toPx = m / DisplayMetrics.DENSITY_DEFAULT
        toDp = DisplayMetrics.DENSITY_DEFAULT / m
        imm = app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    /**
     * @param type Resource type to find. For example, "string".
     * @param name The name of the desired resource. For example, "app_icon".
     * @return int The associated resource identifier. For example, R.string.app_icon.
     */
    @JvmStatic fun resS2I(type:String, name:String):Int = res.getIdentifier(name, type, packName)
    @JvmStatic fun drawable(v: String): Drawable{
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) res.getDrawable(resS2I("drawable", v), null)
        else res.getDrawable(resS2I("drawable", v))
    }
    @JvmStatic fun resDrawable(v: String):Int = resS2I("drawable", v)
    @JvmStatic fun resId(v: String):Int = resS2I("id", v)
    @JvmStatic fun resLayout(v: String):Int = resS2I("layout", v)
    @JvmStatic fun resName(id:Int):String = res.getResourceEntryName(id)
    @JvmStatic fun appVersion():String = app.packageManager.getPackageInfo(app.packageName, 0).versionName
}