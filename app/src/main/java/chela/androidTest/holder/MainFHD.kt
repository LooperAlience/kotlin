package chela.androidTest.holder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import chela.androidTest.R
import chela.androidTest.routerf
import chela.kotlin.Ch
import chela.kotlin.view.router.holder.ChFragmentInfo
import chela.kotlin.view.router.holder.ChHolder
import chela.kotlin.view.router.holder.ChHolderBase

class F:Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):View{
        return inflater.inflate(R.layout.main, container, false)
    }
}
object MainFHD:ChHolder<ChFragmentInfo>(){
    val info = ChFragmentInfo(
        fragment = F()
    )
    override fun create(base: ChHolderBase<ChFragmentInfo>): ChFragmentInfo  = info
    override fun push(base: ChHolderBase<ChFragmentInfo>, isRestore:Boolean){
        with(info.fragment) {
            view?.findViewById<TextView>(R.id.textView)?.let {
                with(Ch.prop){
                    view.background(it, "#aaaaff")
                    text.text(it, "fragment1")
                    event.click(it, View.OnClickListener { routerf.push(Step1FHD) })
                }
            }
        }
    }
}