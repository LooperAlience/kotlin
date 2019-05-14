package chela.kotlin.net

import chela.kotlin.Ch
import okhttp3.Response

class ChResponse(private val response:Response?, var err:String? = null){
    var key:String = ""
    var arg:List<Pair<String, Any>>? = null
    val extra = mutableMapOf<String, Any>()
    var result:Any = ""
    val state:Int by Ch.slazy{response?.code() ?: 0}
    private var isOpened = response == null
    val body:String? by Ch.slazy{
        if(isOpened) null
        else{
            isOpened = true
            response?.body()?.use{it.string()}
        }
    }
    val byte:ByteArray? by Ch.slazy{
        if(isOpened) null
        else {
            isOpened = true
            response?.body()?.use{it.bytes()}
        }
    }
    fun header(k:String) = response?.header(k)
}