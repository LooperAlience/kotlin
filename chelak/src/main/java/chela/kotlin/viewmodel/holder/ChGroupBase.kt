package chela.kotlin.viewmodel.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.viewmodel.holder.ChHolder
import chela.kotlin.viewmodel.holder.ChHolderBase

class ChGroupBase: ChHolderBase<View>(){
    private lateinit var inflater:LayoutInflater
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
    }
    override fun pop(holder: ChHolder<View>) {
        group.removeViewAt(group.childCount - 1)
    }
}
/*
class ActivityBase(act:AppCompatActivity):HolderBase()
class FragmentBase(fragment:Fragment):HolderBase()
 */