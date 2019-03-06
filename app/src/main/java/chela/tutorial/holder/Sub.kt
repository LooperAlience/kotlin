package chela.tutorial.holder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chela.kotlin.view.router.holder.ChHolderBase
import chela.tutorial.App
import chela.tutorial.R
import chela.tutorial.viewmodel.Holder
import chela.tutorial.viewmodel.SubVM


object Sub : Scene() {
    @JvmStatic private val vm = SubVM
    override fun layout() = R.layout.activity_sub
    override fun init(){
        val adapter = ListAdapter()
        adapter.list = vm.list
        scan?.let{
            it.view.findViewById<RecyclerView>(R.id.list).adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }
    override fun push(base: ChHolderBase<View>, isRestore: Boolean){
        if(isRestore){
            vm.pushed()
            render()
        }else{
            App.looper{
                time = Holder.pushTime
                block = {
                    vm.holder.pushAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.pushed()
                    vm.isLock = false
                }
            }
        }
    }
    override fun pop(base: ChHolderBase<View>, isJump:Boolean):Long{
        return if(isJump){
            vm.poped()
            render()
            0L
        }else{
            App.looper{
                time = Holder.popTime
                block = {
                    vm.holder.popAnimation(it)
                    renderSync()
                }
                ended = {
                    vm.poped()
                    renderSync()
                }
            }
            Holder.popTime.toLong()
        }
    }
}

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    var list: List<String> = arrayListOf()
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val number = list[position]
        holder.bind(number)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_list, parent, false))
    }
    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var text: TextView = view.findViewById(R.id.text)

        fun bind(value: String) {
            text.text = value
        }
    }
}
