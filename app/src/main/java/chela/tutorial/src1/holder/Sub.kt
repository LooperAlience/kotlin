package chela.tutorial.src1.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chela.tutorial.R
import chela.tutorial.src1.viewmodel.SubVM


object Sub : Scene() {
    override fun vm() = SubVM
    override fun layout() = R.layout.activity_sub
    override fun init(){
        val adapter = ListAdapter()
        adapter.list = vm().list
        scan?.let{
            it.view.findViewById<RecyclerView>(R.id.list).adapter = adapter
        }
        adapter.notifyDataSetChanged()
    }
    override fun pushed() {}

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
