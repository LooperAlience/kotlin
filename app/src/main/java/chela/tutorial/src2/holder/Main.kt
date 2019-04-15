package chela.tutorial.src2.holder

import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.Scene
import chela.tutorial.src2.Act
import chela.tutorial.src2.viewmodel.MainVM2
import com.bumptech.glide.Glide


object Main : Scene() {
    override fun vm() = MainVM2
    override fun layout() = R.layout.activity_main2
    override fun init(){
        if(!Act.isPermitted) return
        //todo 퍼미션 호출 다시 해야 함
        ChThread.pool(Runnable {
            val list = mutableListOf<Data>()
            Ch.content.getImage(MediaStore.Images.Media._ID, false, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED)?.let{
                if(it.count > 0 && it.moveToFirst()) {
                    val pathIdx = it.getColumnIndex(MediaStore.Images.Media.DATA)
                    val dateIdx = it.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
                    do {
                        list.add(Data(it.getString(pathIdx), it.getString(dateIdx)))
                    } while (it.moveToNext())
                }
                it.close()
            }
            ChThread.main(Runnable {
                val adapter = ListAdapter(list)
                scan?.let {
                    it.view.findViewById<RecyclerView>(R.id.list).adapter = adapter
                }
                adapter.notifyDataSetChanged()
            })
        })

    }
    override fun pushed(){}
}

data class Data(val path:String, val date:String)

class ListAdapter(private val list:List<Data>) : RecyclerView.Adapter<ListAdapter.ImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_wrap, parent, false))
    }
    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        holder.bind(list[position])
    }
    override fun getItemCount() = list.size

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var itemImg: ImageView = itemView.findViewById(R.id.item_img)
        var type: TextView = itemView.findViewById(R.id.type)
        init {
            itemImg.setOnClickListener(this)
        }
        fun bind(d: Data) {
            Glide.with(ChApp.app).load(d.path).into(itemImg)
            type.text = d.date
        }
        override fun onClick(v: View) {
        }
    }
}


