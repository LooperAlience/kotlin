package chela.kotlin.viewmodel.scanner

import android.util.Log
import android.view.View
import android.view.ViewGroup
import chela.kotlin.Ch
import chela.kotlin.core._shift
import chela.kotlin.viewmodel.property.ChProperty
import chela.kotlin.viewmodel.ChViewModel

object ChScanner{
    class Item internal constructor(@JvmField var view:View, private val pos:List<Int>): ChViewModel(){
        @JvmField internal val collector = mutableMapOf<String, Any>()
        private var prop:MutableMap<String, List<String>>? = null
        private var propVal:MutableMap<String, Any>? = null
        private var record:MutableMap<String, List<String>>? = null
        private var recordVal:MutableMap<String, Any>? = null
        private var updater:MutableMap<String, Any>? = null
        private var once:MutableMap<String, Any>? = null
        private var isOnce = false
        internal fun view(v:View){
            var t = v
            for(i in pos) t = (t as ViewGroup).getChildAt(i)
            view = t
            propVal?.clear()
            recordVal?.clear()
            isOnce = false
        }
        override operator fun set(k:String, v:Any):Boolean{
            if(v === ChViewModel.OBJECT ||v === ChViewModel.ARRAY) return true
            if(k[0] == '@'){
                if(updater == null) updater = mutableMapOf()
                updater?.put(k._shift(), v)
            }else{
                if(once == null) once = mutableMapOf()
                once?.put(k, v)
            }
            return true
        }
        override fun viewmodel(k:String, v: List<String>):Boolean{
            if(k[0] == '-'){
                if(once == null) once = mutableMapOf()
                once?.put(k._shift(), Ch.vm.viewmodel(v))
            }else{
                if(prop == null){
                    prop = mutableMapOf()
                    propVal = mutableMapOf()
                }
                prop?.put(k, v)
            }
            return true
        }
        override fun record(k:String, v: List<String>):Boolean{
            if(record == null){
                record = mutableMapOf()
                recordVal = mutableMapOf()
            }
            record?.put(k, v)
            return true
        }
        fun render(recordViewModel: ChViewModel? = null):Boolean{
            var isRender = false
            collector.clear()
            if(!isOnce){
                isOnce = true
                once?.let{
                    isRender = true
                    collector.putAll(it)
                }
            }
            updater?.let{
                isRender = true
                collector.putAll(it)
            }
            prop?.let{
                it.forEach {(k, _v) ->
                    val v = Ch.vm.viewmodel(_v)
                    if(k[0] == '@'){
                        collector[k._shift()] = v
                        isRender = true
                    }else propVal?.let{
                        if(it[k] == null || it[k] != v) collector[k] = v
                        it[k] = v
                        isRender = true
                    }
                }
            }
            record?.let{record->
                recordViewModel?.let{collector.putAll(record.mapValues{ (_, v)->
                    Ch.vm.record(v, it)
                }.filter ch@{ (k, v)->
                    recordVal?.let{
                        it[k]?.let{if(it == v) return@ch false}
                        it.put(k, v)
                        isRender = true
                    }
                    return@ch true
                }
                )}}
            return isRender
        }
    }
    class Scanned internal constructor(@JvmField var view:View, private val items:MutableSet<Item> = mutableSetOf()):MutableSet<Item> by items{
        private val collector = mutableSetOf<Item>()
        fun render(v:View? = null):View{
            val isNew = v != null && v !== view
            if(isNew) view = v!!
            collector.clear()
            items.forEach{
                if(isNew) it.view(view)
                if(it.render()) collector += it
            }
            if(collector.isNotEmpty()) Ch.thread.msg(Ch.thread.property, collector)
            return view
        }
        fun renderSync(){
            items.forEach{
                if(it.render()){
                    val view = it.view
                    it.collector.forEach{(k, v)-> ChProperty.f(view, k.toLowerCase(), v)}
                }
            }
        }
    }
    @JvmStatic private val scanned = mutableMapOf<Any, Scanned>()
    @JvmStatic operator fun get(k:Any): Scanned? = scanned[k]
    @JvmStatic fun scan(id:Any, view:View): Scanned {
        val prev = scanned[id]
        if(prev != null && prev.view == view) return prev
        val st = mutableListOf(view)
        val result = Scanned(view)
        scanned[id] = result
        while(st.isNotEmpty()){
            val v = st.removeAt(st.size - 1)
            if(v.tag != null && v.tag is String){
                val pos = mutableListOf<Int>()
                var t = v
                while (t !== view) v.parent?.let {
                    val p = it as ViewGroup
                    pos += p.indexOfChild(v)
                    t = p
                }
                val target = Item(v, pos)
                target.fromJson("{${v.tag}}")
                result += target
            }
            if (v is ViewGroup) for (i in v.childCount - 1 downTo 0) st.add(v.getChildAt(i))
        }
        return result
    }
}
