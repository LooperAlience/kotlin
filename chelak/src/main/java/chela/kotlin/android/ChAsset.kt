package chela.kotlin.android

import chela.kotlin.Ch
import java.nio.charset.Charset

object ChAsset{
    @JvmStatic private val asset = mutableMapOf<String, ByteArray>()
    @JvmStatic fun bytes(path:String):ByteArray = asset[path]?.let{it} ?: try{
        Ch.app.asset.open(path).use{
            val size = it.available()
            val buf = ByteArray(size)
            it.read(buf)
            asset[path] = buf
            buf
        }
    }catch(e:Throwable){
        Ch.NONE_BA
    }
    @JvmStatic fun bytes(path: String, block:(ByteArray)->Unit){Ch.thread.que(Runnable{block(bytes(path))})}
    @JvmStatic fun string(path: String):String = bytes(path).toString(Charset.defaultCharset())
    @JvmStatic fun string(path: String, block:(String)->Unit){Ch.thread.que(Runnable{block(string(path))})}
}