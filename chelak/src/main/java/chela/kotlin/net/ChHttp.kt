package chela.kotlin.net

import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.thread.ChThread
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class ChHttp{
    companion object {
        val EXTRA_JSON = "json"
        val EXTRA_REQUEST = "request"
    }
    val extra = mutableMapOf<String, Any>()
    abstract fun header(key:String, value:String): ChHttp
    abstract fun form(key:String, value:String): ChHttp
    abstract fun json(json:String): ChHttp
    abstract fun body(body:String): ChHttp
    abstract fun body(body:ByteArray): ChHttp
    abstract fun file(key: String, filename: String, mine: String, file: ByteArray): ChHttp
    abstract fun send(block: httpCallBack)
}
private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(3, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
    .build()
private val JSON = MediaType.parse("application/json; charset=utf-8")
private val BODY = MediaType.parse("plain/text; charset=utf-8")
private val BODYBYTE = MediaType.parse("application/octet-stream; charset=utf-8")
class ChHttpOk3 internal constructor(private val method:String, private var request:Request.Builder):ChHttp(){
    private var form: FormBody.Builder? = null
    private var json:String? = null
    private var body:String? = null
    private var bodyByte:ByteArray? = null
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
    override fun body(body:String): ChHttp {
        this.body = body
        return this
    }
    override fun body(body:ByteArray): ChHttp {
        this.bodyByte = body
        return this
    }
    override fun file(key:String, filename:String, mine:String, file:ByteArray): ChHttp {
        if(multi == null) multi = MultipartBody.Builder().setType(MultipartBody.FORM)
        multi?.addFormDataPart(key, filename, RequestBody.create(MediaType.parse(mine), file))
        return this
    }
    override fun send(block:httpCallBack){
        if(method != "GET")(
            multi?.let{multi->
                bodyByte?.let {multi.addPart(RequestBody.create(BODYBYTE, it))} ?:
                json?.let {multi.addPart(RequestBody.create(JSON, it))} ?:
                body?.let {multi.addPart(RequestBody.create(BODY, it))} ?:
                form?.let {multi.addPart(it.build())}
                multi.build()
            } ?:
            bodyByte?.let{RequestBody.create(BODYBYTE, it)} ?:
            json?.let{RequestBody.create(JSON, it)} ?:
            body?.let{RequestBody.create(BODY, it)} ?:
            form?.build()
        )?.let{request = request.method(method, it)}
        if(Ch.debugLevel > 1) Log.i("ch", "$request")
        okHttpClient.newCall(request.build()).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) = block(ChResponse(null, e.toString()))
            override fun onResponse(call: Call, response:Response) = block(ChResponse(response))
        })
    }
}