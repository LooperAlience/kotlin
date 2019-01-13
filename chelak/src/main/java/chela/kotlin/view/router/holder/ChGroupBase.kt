package chela.kotlin.view.router.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class ChGroupBase: ChHolderBase<View>(){
    lateinit var inflater:LayoutInflater
    lateinit var group:ViewGroup
    fun group(it:ViewGroup){
        group = it
        inflater = (group.context as AppCompatActivity).layoutInflater
        group.removeAllViews()
        restore()
    }
    fun inflate(id:Int, isMerge:Boolean = false):View = inflater.inflate(id, group, isMerge)
    override fun push(holder: ChHolder<View>){
        group.addView(holder.create(this))
        group.visibility = View.VISIBLE
    }
    override fun pop(holder: ChHolder<View>){
        group.removeViewAt(group.childCount - 1)
        if(group.childCount == 0) group.visibility = View.GONE
    }
}