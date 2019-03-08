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
            val projection = arrayOf( MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns._ID, MediaStore.Images.Media.DATE_ADDED)
            val contentResolver = Ch.app.app.getContentResolver()
            val imageCursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // content://로 시작하는 content table uri
                projection, // 어떤 row를 출력할 것인지
                null, null,
                MediaStore.Images.Media._ID + " DESC"
            )// 어떤 column을 출력할 것인지
            val dataColumnIndex = imageCursor.getColumnIndex(projection[0])
            val dataColumnIndex_date = imageCursor.getColumnIndex(projection[2])
            if (imageCursor == null) {/* Error 발생 */
                Log.i("ch", "ch111")
            } else if (imageCursor.moveToFirst()) {
                do {
                    val filePath = imageCursor.getString(dataColumnIndex)
                    val fileDate = imageCursor.getString(dataColumnIndex_date)
                    Ch.sql.db("img").exec("local_add", "filePath" to filePath, "fileDate" to fileDate)
                } while (imageCursor.moveToNext())
            } else { /*imageCursor가 비었습니다.*/
            }
            imageCursor.close()

            Ch.thread.main(Runnable {
                adapter.list = Ch.sql.db("img").select("local_list")?.rs
                adapter.notifyDataSetChanged()
            })
        })

    }
    override fun pushed(){}
}


class ListAdapter : RecyclerView.Adapter<ListAdapter.ImageHolder>() {
    var list:Array<Array<Any?>>? = null

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


