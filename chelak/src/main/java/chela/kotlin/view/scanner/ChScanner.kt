package chela.kotlin.view.scanner

import android.view.View
import android.view.ViewGroup
import chela.kotlin.Ch

/**
 * This object scans the View's items. Here, items refers to ChScanItem.
 * If the view in layout.xml has the value of android:tag,
 * then add it to the items of ChScanned.
 */
object ChScanner{
    private val scanned = mutableMapOf<Any, ChScanned>()
    operator fun get(k:Any): ChScanned? = scanned[k]
    fun scan(id:Any, view:View): ChScanned{
        val prev = scanned[id]
        if(prev != null && prev.view == view) return prev
        val st = mutableListOf(view)
        val result = ChScanned(view)
        if(Ch.NONE != id) scanned[id] = result
        var limit = 200
        while(st.isNotEmpty()&& limit-- > 0){
            val v = st.removeAt(st.size - 1)
            if(v.tag != null && v.tag is String){
                val pos = mutableListOf<Int>()
                var t = v
                var limit = 30
                while(t !== view && limit-- > 0){
                    t.parent?.let {
                        val p = it as ViewGroup
                        pos += p.indexOfChild(v)
                        t = p
                    }
                }
                val target = ChScanItem(v, pos)
                val tag = "${v.tag}"

                target.fromJson("{${if("@`$".contains(tag[0])) "style:" else ""}$tag}")
                result += target
            }
            if (v is ViewGroup) for (i in v.childCount - 1 downTo 0) st.add(v.getChildAt(i))
        }
        return result
    }
}
