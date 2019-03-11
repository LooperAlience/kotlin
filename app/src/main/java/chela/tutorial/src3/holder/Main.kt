package chela.tutorial.src3.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import chela.tutorial.R
import chela.tutorial.src3.viewmodel.MainVM


object Main : Scene() {
    override fun vm() = MainVM
    override fun layout() = R.layout.activity_main3
    override fun init(){
        val imagePagerAdapter = ImagePagerAdapter(intArrayOf(R.drawable.guide0, R.drawable.guide1, R.drawable.guide2, R.drawable.guide3))
        scan?.let {
            it.view.findViewById<ViewPager>(R.id.viewpager).adapter = imagePagerAdapter
        }
    }
    override fun pushed(){}
}

class ImagePagerAdapter(private var mResources: IntArray) : PagerAdapter() {
    override fun getCount(): Int = mResources.size
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as View
    }
    override fun instantiateItem(parent: ViewGroup, position: Int): Any {
        return LayoutInflater.from(parent.context).inflate(R.layout.row_image_wrap, parent,false).apply {
            findViewById<ImageView>(R.id.item_img).apply{
                setImageResource(mResources[position])
            }
            parent.addView(this)
        }
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
