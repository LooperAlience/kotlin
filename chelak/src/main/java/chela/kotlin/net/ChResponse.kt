package chela.kotlin.net

import okhttp3.Response

class ChResponse(
    var body:String?, var err:String?, var state:Int, var response: Response?,
    var key:String = "", var arg:List<Pair<String, Any>>? = null
){
    var result:Any = "$body"
    val extra = mutableMapOf<String, Any>()
}