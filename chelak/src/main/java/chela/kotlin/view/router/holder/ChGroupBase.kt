package chela.kotlin.view.router.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity

class ChGroupBase: ChHolderBase<View>(){
    lateinit var inflater:LayoutInflater
    lateinit var group:ViewGroup
    fun group(it:ViewGroup, removeChildren:Boolean = true){
        group = it
        inflater = (group.context as AppCompatActivity).layoutInflater
        if(removeChildren) group.removeAllViews()
    }
    fun inflate(id:Int, isMerge:Boolean = false) = inflater.inflate(id, group, isMerge)!!

    override fun push(holder:ChHolder<View>, isRestore:Boolean){
        group.addView(holder.create(this, isRestore))
        group.visibility = View.VISIBLE
    }
    override fun pop(holder: ChHolder<View>, isJump:Boolean){
        if(group.childCount > 0) group.removeViewAt(group.childCount - 1)
        if(group.childCount == 0) group.visibility = View.GONE
    }
    override fun take(index:Int, holder: ChHolder<View>){
        if(group.childCount > index) group.removeViewAt(index)
        if(group.childCount == 0) group.visibility = View.GONE
    }
    override fun clear() {
        group.removeAllViews()
    }
}