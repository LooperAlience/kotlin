package chela.kotlin.view.router.holder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch
import chela.kotlin.looper.ChLooper
import chela.kotlin.view.router.scanholder.ChScanHolder

class ChGroupBase: ChHolderBase<View>(){
    companion object{var looper: ChLooper = ChLooper()}
    private var inflater:LayoutInflater? = null
    private var group:ViewGroup? = null
    fun group(it:ViewGroup, removeChildren:Boolean = true){
        newId()
        looper.act(it.context as AppCompatActivity)
        group = it
        if(removeChildren) clear()
        (it.context as? AppCompatActivity)?.let{inflater = it.layoutInflater} ?: throw Throwable("invalid AppCompatActivity - ${it.context}")
    }
    fun inflate(id:Int, isMerge:Boolean = false) = inflater?.inflate(id, group, isMerge)!!
    override fun add(holder:ChHolder<View>, isBottom:Boolean){
        group?.let{
            it.visibility = View.VISIBLE
            val v = create(holder)
            if(isBottom) it.addView(v, 0)
            else it.addView(v)
        }
    }
    override fun remove(holder:ChHolder<View>, t:View){
        group?.let{
            it.removeView(t)
            if(it.childCount == 0) it.visibility = View.GONE
        }
    }
    override fun clear(){
        group?.removeAllViews()
    }
}