package chela.kotlin.android

import chela.kotlin.Ch
import chela.kotlin.thread.ChThread
import java.nio.charset.Charset

object ChAsset{
    private val asset = mutableMapOf<String, ByteArray>()
    fun bytes(path:String):ByteArray = asset[path]?.let{it} ?: try{
        ChApp.asset.open(path).use{
            val size = it.available()
            val buf = ByteArray(size)
            it.read(buf)
            asset[path] = buf
            buf
        }
    }catch(e:Throwable){
        Ch.NONE_BA
    }
    fun bytes(path: String, block:(ByteArray)->Unit){ChThread.que(Runnable{block(bytes(path))})}
    fun string(path: String):String = bytes(path).toString(Charset.defaultCharset())
    fun string(path: String, block:(String)->Unit){ChThread.que(Runnable{block(string(path))})}
}