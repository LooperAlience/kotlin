package chela.androidTest.holder

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.TextView
import chela.androidTest.R
import chela.androidTest.groupBase
import chela.androidTest.looper
import chela.androidTest.router
import chela.kotlin.Ch
import chela.kotlin.looper.ChLooper.Item.Ended
import chela.kotlin.looper.ChLooper.Item.Time
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.viewmodel.holder.ChHolder
import chela.kotlin.viewmodel.holder.ChHolderBase
import chela.test.viewmodel.Step1VM

@SuppressLint("StaticFieldLeak")
object Step1HD: ChHolder<View>(){
    lateinit var view:View
    override fun create(base: ChHolderBase<View>): View{
        if(base !is ChGroupBase) throw Exception("invalid base:$base")
        val v =  base.inflate(R.layout.main)
        view = v
        v.setBackgroundColor(Color.parseColor("#99ff99"))
        v.findViewById<TextView>(R.id.textView)?.let{
            it.setTextColor(Color.parseColor("#6600ff"))
            it.isClickable = true
            it.setOnClickListener{router.pop()}
        }
        return v
    }
    override fun push(base: ChHolderBase<View>, isRestore:Boolean){
        Ch.net("GET", "https://www.bsidesoft.com/hika/chela/test.json")
        .send{data, _, _->
            data?.let{
                Step1VM.fromJson(it)
                view.findViewById<TextView>(R.id.textView)?.let{
                    it.text = Step1VM.userid
                }
            }
        }
    }
    override fun pop(base: ChHolderBase<View>, isJump: Boolean):Boolean {
        val w = -groupBase.group.width.toDouble()
        looper.invoke(Time(350), Ended{base.pop(this)}) {
            Ch.prop.view.x(view, it.backIn(0.0, w))
        }
        return false
    }
}