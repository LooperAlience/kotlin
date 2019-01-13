package chela.kotlin.net

import okhttp3.Response

class ChResponse(
    var body:String?, var err:String?, val state:Int, private val response: Response?,
    var key:String = "", var arg:List<Pair<String, Any>>? = null
){
    var result:Any = "$body"
    val extra = mutableMapOf<String, Any>()
    fun header(k:String) = response?.header(k)
}