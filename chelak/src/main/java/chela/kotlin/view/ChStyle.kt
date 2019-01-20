package chela.kotlin.view

import android.graphics.Typeface
import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.android.ChApp
import chela.kotlin.net.ChNet
import chela.kotlin.view.ChStyle.items
import java.io.File
import java.io.FileOutputStream

/**
 * This object cached style property on [items].
 */
typealias fontL = (Typeface)->Unit
object ChStyle{
    @JvmStatic val items = mutableMapOf<String, Map<String, Any>>()
    @JvmStatic private val fonts = mutableMapOf<String, Typeface>()
    @JvmStatic private val fontListener = mutableMapOf<String, MutableList<fontL>>()
    @JvmStatic fun add(k:String, map:Map<String, Any>){items[k] = map}
    @JvmStatic fun remove(k:String) = items.remove(k)
    @JvmStatic operator fun get(k:String):Map<String, Any>? = items[k]
    @JvmStatic fun getFont(k:String, block:fontL):Boolean = fonts[k]?.let{
        if(it === Typeface.DEFAULT){
            if(fontListener[k] == null) fontListener[k] = mutableListOf()
            fontListener[k]?.let{it += block}
        }else block(it)
        true
    } ?: false
    @JvmStatic fun addFont(k:String, path:String){
        if(path.startsWith("http")){
            val f = File(ChApp.fileDir, "ch_font_$k")
            Log.i("ch", "font file:$f, ${f.length()}")
            if(f.exists() && f.length() > 0L) addFont(k, f)
            else{
                fonts[k] = Typeface.DEFAULT
                ChNet.http("GET", path).send{res ->
                    res.byte?.let { data ->
                        if(f.createNewFile()) FileOutputStream(f).use{
                            it.write(data)
                        }
                        Log.i("ch", "sdsdsd:${f.exists()}, ${f.length()}")
                        addFont(k, f)
                        fonts[k]?.let{font->fontListener[k]?.let{
                            it.forEach{it(font)}}
                            fontListener.remove(k)
                        }
                    }
                }
            }
        }else fonts[k] = Typeface.createFromAsset(Ch.app.asset, path)
    }
    @JvmStatic fun addFont(k:String, file: File) = fonts.put(k, Typeface.createFromFile(file))

    @JvmStatic fun removeFont(k:String) = fonts.remove(k)
}