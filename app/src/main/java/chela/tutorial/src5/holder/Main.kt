package chela.tutorial.src5.holder

import android.util.Log
import chela.kotlin.Ch
import chela.kotlin.core._forObject
import chela.kotlin.core._try
import chela.kotlin.model.Model
import chela.kotlin.resource.Cdata
import chela.tutorial.R
import chela.tutorial.common.Scene
import chela.tutorial.src5.viewmodel.MainVM5
import org.json.JSONObject


object Main : Scene(){
    override fun vm() = MainVM5
    override fun layout() = R.layout.activity_main5
    override fun init(){
        val data = """{
            "test@ln":{
                "ko":"안녕하세요.",
                "en@a":{
                    "a":"hello"
                }
            },
            "test2@ln":{
                "ko":"안녕하세요."
            }
        }"""
        val json = _try{JSONObject(data)}?.let{
            it._forObject { key, obj ->Cdata(key, obj)}
        }
        Ch.cdata.invoke("@ln", "en")
        Ch.cdata.invoke("@a", "b")
        if(Ch.query.chId("test=[@ln=en&@a=b]") == 0){
            Ch.query.chAdd("test=[@ln=en&@a=b]", """{
                "test@ln":{
                  "en@a":{
                     "b":"hello"
                  }
               }
            }""")
        }
        val m = object:Model(){
            override fun set(k:String, v:Any):Boolean{
                Log.i("ch", "$k - $v")
                return true
            }
        }
        m.fromJson("""{
            t:"wer",
            a:3ce,
            b:1223
        }""")

    }
    override fun pushed(){ }
    private fun updateCdata(block:()->Unit){
        Ch.cdata.requestKey?.let{ k->
            Log.i("ch", k)
            Ch.net.http("POST", "https://seller2.bsidesoft.com/api/__json/cdata/test.php")
                .send {
                    it.body?.let{
                        Ch.cdata.save(k, it)?.let{
                            Log.i("ch", "error - $it")
                        } ?: block()

                    }
                }
        } ?: block()
    }
    fun click() {
        updateCdata{
            render()
        }
    }
}