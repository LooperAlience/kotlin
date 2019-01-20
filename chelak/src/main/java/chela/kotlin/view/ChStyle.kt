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
object ChStyle{
    @JvmStatic val items = mutableMapOf<String, Map<String, Any>>()
    @JvmStatic val fonts = mutableMapOf<String, Typeface>()
    @JvmStatic fun add(k:String, map:Map<String, Any>){items[k] = map}
    @JvmStatic fun remove(k:String) = items.remove(k)
    @JvmStatic operator fun get(k:String):Map<String, Any>? = items[k]
    @JvmStatic fun addFont(k:String, path:String){
        if(path.startsWith("http")){
            val f = File(ChApp.fileDir, "ch_font_$k")
            if(!f.exists()) addFont(k, f)
            else {
                ChNet.http("GET", path).send{res ->
                    res.byte?.let { data ->
                        if(f.createNewFile()) FileOutputStream(f).use{
                            it.write(data)
                            it.close()
                        }
                        addFont(k, f)
                    }
                }
            }
        }else fonts[k] = Typeface.createFromAsset(Ch.app.asset, path)
    }
    @JvmStatic fun addFont(k:String, file: File) = fonts.put(k, Typeface.createFromFile(file))

    @JvmStatic fun removeFont(k:String) = fonts.remove(k)
}