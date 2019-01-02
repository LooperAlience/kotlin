package chela.kotlin.view.scanner

import android.view.View
import android.view.ViewGroup

object ChScanner{
    @JvmStatic private val scanned = mutableMapOf<Any, ChScanned>()
    @JvmStatic operator fun get(k:Any): ChScanned? = scanned[k]
    @JvmStatic fun scan(id:Any, view:View): ChScanned {
        val prev = scanned[id]
        if(prev != null && prev.view == view) return prev
        val st = mutableListOf(view)
        val result = ChScanned(view)
        scanned[id] = result
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
                target.fromJson("{${v.tag}}")
                result += target
            }
            if (v is ViewGroup) for (i in v.childCount - 1 downTo 0) st.add(v.getChildAt(i))
        }
        return result
    }
}