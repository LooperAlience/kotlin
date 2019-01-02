package chela.androidTest.holder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import chela.androidTest.Hika
import chela.androidTest.R
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChFragmentInfo
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase

class F1:Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View{
        return inflater.inflate(R.layout.main, container, false)
    }
}
object Step1FHD:ChHolder<ChFragmentInfo>(){
    val info = ChFragmentInfo(
        fragment = F1()
    )
    override fun create(base: ChHolderBase<ChFragmentInfo>): ChFragmentInfo  = info
    override fun push(base: ChHolderBase<ChFragmentInfo>, isRestore:Boolean){
        with(info.fragment) {
            view?.findViewById<TextView>(R.id.textView)?.let {
                with(Ch.prop){
                    view.background(it, "#aaffaa")
                    text.text(it, "fragment2")
                    event.click(it, View.OnClickListener {
                        startActivity(Intent(context, Hika::class.java))
                    })
                }
            }
        }
    }
}