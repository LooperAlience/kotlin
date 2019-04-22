package chela.kotlin.android

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.content.res.AppCompatResources
import chela.kotlin.view.ChWindow
import java.io.File
import java.util.*

/**
 * Base object for accessing an application's resource and converting display unit.
 */
object ChApp{
    lateinit var app: Application
    val clip: ClipboardManager by lazy{app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager}
    lateinit var res: Resources
    lateinit var asset: AssetManager
    lateinit var cm: ConnectivityManager
    lateinit var imm: InputMethodManager
    lateinit var packName:String
    lateinit var dm: DisplayMetrics
    lateinit var fileDir:File
    lateinit var cacheDir: File

    lateinit var locale: Locale
    val language:String get() = locale.language

    operator fun invoke(a:Application){
        app = a
        fileDir = a.filesDir!!
        cacheDir = a.cacheDir!!
        res = a.resources
        asset = a.assets
        packName = a.packageName

        @Suppress("DEPRECATION")
        locale = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) res.configuration.locales[0]
            else res.configuration.locale
        imm = app.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        dm = res.displayMetrics
        val d = dm.density.toDouble()
        ChWindow.DptoPx = d
        ChWindow.PxtoDp = 1 / d
        val s = dm.scaledDensity.toDouble()
        ChWindow.SptoPx = s
        ChWindow.PxtoSp = 1 / s
    }
    fun appVersion():String = app.packageManager.getPackageInfo(app.packageName, 0).versionName

    fun deviceId() = Settings.Secure.ANDROID_ID
    fun deviceModel() = Build.MODEL
    fun deviceVersion() = Build.VERSION.RELEASE
    /**
     * @param type Resource type to find. For example, "string".
     * @param name The name of the desired resource. For example, "app_name".
     * @return int The associated resource identifier. For example, R.string.app_name.
     */
    fun resS2I(type:String, name:String):Int = res.getIdentifier(name, type, packName)
    fun resDrawable(v: String):Int = resS2I("drawable", v)
    fun resId(v: String):Int = resS2I("id", v)
    fun resLayout(v: String):Int = resS2I("layout", v)
    fun resFont(v: String):Int = resS2I("font", v)
    fun resName(id:Int):String = res.getResourceEntryName(id)
    fun drawable(v:String):Drawable? = drawable(resS2I("drawable", v))
    fun drawable(v:Int):Drawable? = AppCompatResources.getDrawable(app, v)
    fun bitmap2Drawable(v:Bitmap): BitmapDrawable = BitmapDrawable(res, v)
    fun string(v:String):String = string(resS2I("string", v))
    fun string(v:Int):String = res.getString(v)
}