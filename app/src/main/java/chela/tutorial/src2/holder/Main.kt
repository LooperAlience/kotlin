package chela.tutorial.src2.holder

import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import chela.kotlin.Ch
import chela.kotlin.resource.ChRes
import chela.kotlin.sql.ChSql
import chela.kotlin.sql.DataBase
import chela.tutorial.R
import chela.tutorial.src1.holder.Sub
import chela.tutorial.src2.App
import chela.tutorial.src2.viewmodel.MainVM
import com.bumptech.glide.Glide
import java.util.ArrayList
import java.util.HashMap


object Main : Scene() {
    override fun vm() = MainVM
    override fun layout() = R.layout.activity_main
    override fun init(){
        val adapter = ListAdapter()
        scan?.let{
            it.view.findViewById<RecyclerView>(R.id.list).adapter = adapter
        }
        if(!App.isPermitted) return
        //todo 퍼미션 호출 다시 해야 함

        """
        local_img_create--CREATE TABLE IF NOT EXISTS local_img(
            rowid INTEGER PRIMARY KEY AUTOINCREMENT,
            filePath VARCHAR(255) NOT null,
            fileDate VARCHAR(255) NOT null
        );
        local_add--insert into local_img(filePath,fileDate)values(@filePath:string@,@fileDate:string@);
        local_remove_all--delete from local_img;
        local_list--select * from local_img
        """.split(";").forEach {
            val a = it.split("--")
            Ch.sql.addQuery(a[0].trim(), a[1].trim())
        }
        Ch.sql.addDb("img", "local_img_create", null, null)
        Ch.sql.db("img").exec("local_remove_all")

        Ch.thread.pool(Runnable {
            val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED)
            val imageCursor = Ch.app.app.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null,MediaStore.Images.Media._ID + " DESC")

            imageCursor?.let{
                it.moveToFirst()
                val pathIdx = it.getColumnIndex(projection[0])
                val dateIdx = it.getColumnIndex(projection[1])
                do {
                    Ch.sql.db("img").exec("local_add", "filePath" to imageCursor.getString(pathIdx), "fileDate" to imageCursor.getString(dateIdx))
                } while (imageCursor.moveToNext())
                it.close()
            }
            Ch.thread.main(Runnable {
                Ch.sql.db("img").select("local_list")?.map{_,arr->Data("${arr[0]}", "${arr[1]}")}?.let {
                    adapter.list = it
                    adapter.notifyDataSetChanged()
                }
            })
        })
    }
    override fun pushed(){}
}
data class Data(val path:String, val date:String)

class ListAdapter : RecyclerView.Adapter<ListAdapter.ImageHolder>() {
    lateinit var list:List<Data>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_image_wrap, parent, false))
    }
    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        list?.let {
            holder.bind(it[position])
        }
    }
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var item_img: ImageView = itemView.findViewById(R.id.item_img)
        var type: TextView = itemView.findViewById(R.id.type)
        init {
            item_img.setOnClickListener(this)
        }
        fun bind(i: Array<Any?>) {
            Glide.with(Ch.app.app).load(i[1]).into(item_img)
            type.text = i[2] as String
        }
        override fun onClick(v: View) {
        }
    }
}


