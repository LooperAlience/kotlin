package chela.kotlin.view

import android.graphics.Typeface
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
    val items = mutableMapOf<String, Map<String, Any>>()
    private val fonts = mutableMapOf<String, Typeface>()
    private val fontListener = mutableMapOf<String, MutableList<fontL>>()
    fun add(k:String, map:Map<String, Any>){
        items[k] = map
    }
    fun remove(k:String) = items.remove(k)
    operator fun get(k:String):Map<String, Any>? = items[k]
    fun getFont(k:String, block:fontL):Boolean = fonts[k]?.let{
        if(it === Typeface.DEFAULT){
            if(fontListener[k] == null) fontListener[k] = mutableListOf()
            fontListener[k]?.let{it += block}
        }else block(it)
        true
    } ?: false
    fun addFont(k:String, path:String){
        if(path.startsWith("http")){
            val f = File(ChApp.fileDir, "ch_font_$k")
            if(f.exists() && f.length() > 0L) addFont(k, f)
            else{
                fonts[k] = Typeface.DEFAULT
                ChNet.http("GET", path).send{res ->
                    res.byte?.let { data ->
                        if(f.createNewFile()) FileOutputStream(f).use{
                            it.write(data)
                        }
                        addFont(k, f)
                        fonts[k]?.let{font->fontListener[k]?.let{
                            it.forEach{it(font)}}
                            fontListener.remove(k)
                        }
                    }
                }
            }
        }else fonts[k] = Typeface.createFromAsset(ChApp.asset, path)
    }
    fun addFont(k:String, file: File) = fonts.put(k, Typeface.createFromFile(file))

    fun removeFont(k:String) = fonts.remove(k)
}