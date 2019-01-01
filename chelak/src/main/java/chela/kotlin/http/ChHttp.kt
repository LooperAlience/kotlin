package chela.kotlin.http

import chela.kotlin.Ch
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
typealias httpCallBack = (body:String?, err:String?, state:Int)->Unit

interface ChHttp{
    fun header(key:String, value:String): ChHttp
    fun form(key:String, value:String): ChHttp
    fun json(json:String): ChHttp
    fun file(key: String, filename: String, mine: String, file: ByteArray): ChHttp
    fun send(callback: httpCallBack)
}
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(3, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
    .build()
private val JSON = MediaType.parse("application/json; charset=utf-8")
class ChHttpOk3 internal constructor(private val method:String, private var request:Request.Builder):ChHttp{
    private var form: FormBody.Builder? = null
    private var json:String? = null
    private var multi: MultipartBody.Builder? = null
    override fun header(key:String, value:String): ChHttp {
        request = request.addHeader(key, value)
        return this
    }
    override fun form(key:String, value:String): ChHttp {
        if(form == null) form = FormBody.Builder()
        form?.add(key, value)
        return this
    }
    override fun json(json:String): ChHttp {
        this.json = json
        return this
    }
    override fun file(key:String, filename:String, mine:String, file:ByteArray): ChHttp {
        if(multi == null) multi = MultipartBody.Builder().setType(MultipartBody.FORM)
        multi?.addFormDataPart(key, filename, RequestBody.create(MediaType.parse(mine), file))
        return this
    }
    override fun send(callback: httpCallBack){
        when(method){
            "POST"->{
                multi?.let {multi->
                    json?.let {multi.addPart(RequestBody.create(JSON, it))} ?:
                    form?.let {multi.addPart(it.build())}
                    request = request.post(multi.build())
                } ?:
                json?.let {request = request.post(RequestBody.create(JSON, it))} ?:
                form?.let {request = request.post(it.build())}
            }
        }
        okHttpClient.newCall(request.build()).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                Ch.thread.main(Runnable{callback(null, e.toString(), 0)})}
            override fun onResponse(call: Call, response: Response){
                val code = response.code()
                response.body()?.let {
                    val b = it.string()
                    response.close()
                    Ch.thread.main(Runnable {callback(b, null, code)})
                } ?: Ch.thread.main(Runnable {callback(null, "body error", code)})
            }
        })
    }
}

