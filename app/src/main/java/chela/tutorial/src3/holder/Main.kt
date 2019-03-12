package chela.tutorial.src3.holder

import android.app.ActionBar
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import chela.kotlin.Ch
import chela.tutorial.R
import chela.tutorial.common.Scene
import chela.tutorial.src3.viewmodel.MainVM3


object Main : Scene() {
    override fun vm() = MainVM3
    override fun layout() = R.layout.activity_main3
    override fun init(){
        Log.i("ch", "init")

        val imagePagerAdapter = ImagePagerAdapter(intArrayOf(R.drawable.guide0, R.drawable.guide1, R.drawable.guide2, R.drawable.guide3))
        val indicatorManager = CircleIndicatorManager(Ch.app.app)

        scan?.let { chScanned ->
            indicatorManager.apply {
                createDotPanel(chScanned.view.findViewById(R.id.indicator), imagePagerAdapter.count)
                selectDot(0)
            }
            chScanned.view.findViewById<ViewPager>(R.id.viewpager).apply{
                adapter = imagePagerAdapter
                addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
                    override fun onPageScrollStateChanged(state: Int) {}
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                    override fun onPageSelected(position: Int) {
                        indicatorManager.selectDot(position)
                    }
                })
            }
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

class CircleIndicatorManager(private val context: Context) {
    private lateinit var imgCircle: Array<ImageView>
    fun createDotPanel(layout: LinearLayout, count: Int) {
        layout.removeAllViews()
        imgCircle = Array(count){ImageView(context)}
        imgCircle.forEach {
            it.layoutParams = LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT).apply {
                leftMargin = 3
                rightMargin = 3
                gravity = Gravity.CENTER
            }
            it.setImageResource(R.drawable.bs_dot_off)
            layout.addView(it)
        }
    }
    fun selectDot(position: Int) {
        imgCircle.forEachIndexed { index, it -> if(index == position) it.setImageResource(R.drawable.bs_dot_on) else it.setImageResource(R.drawable.bs_dot_off)}
    }
}

