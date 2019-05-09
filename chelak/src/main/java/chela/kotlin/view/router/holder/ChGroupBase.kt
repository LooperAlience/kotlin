package chela.kotlin.view.router.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch

class ChGroupBase: ChHolderBase<View>(){
    private val names = mutableMapOf<String, View>()
    private var inflater:LayoutInflater? = null
    private var group:ViewGroup? = null
    fun group(it:ViewGroup, removeChildren:Boolean = true){
        id = Ch.Id()
        group = it
        if(removeChildren) clear()
        (it.context as? AppCompatActivity)?.let{inflater = it.layoutInflater} ?: throw Throwable("invalid AppCompatActivity - ${it.context}")
    }
    fun inflate(id:Int, isMerge:Boolean = false) = inflater?.inflate(id, group, isMerge)!!
    override fun push(holder:ChHolder<View>, isRestore:Boolean){
        group?.let{
            it.visibility = View.VISIBLE
            val v = holder.create(this, isRestore)
            if(holder.name.isNotBlank()) names[holder.name] = v
            it.addView(v)
        }
    }
    override fun pop(holder:ChHolder<View>, isRestore:Boolean){
        group?.let{
            if(it.childCount > 0){
                it.removeViewAt(it.childCount - 1)
                if(holder.name.isNotBlank()) names.remove(holder.name)
                if(it.childCount == 0) it.visibility = View.GONE
            }
        }
    }
    override fun take(holder: ChHolder<View>){
        group?.let{g->
            if(holder.name.isNotBlank()) names[holder.name]?.let{
                g.removeView(it)
                if(g.childCount == 0) g.visibility = View.GONE
            }
        }
    }
    override fun clear(){
        group?.removeAllViews()
        names.clear()
    }
}