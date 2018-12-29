package chela.kotlin.android

import android.content.ContextWrapper
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch


object ChView {
    fun getActivity(view:View):AppCompatActivity?{
        var context = view.context
        var limit = 30
        while(context is ContextWrapper && limit-- > 0){
            if(context is AppCompatActivity) return context
            context = context.baseContext
        }
        return null
    }
    fun cursorPos(view:EditText):Pair<Int, Int>{
        val pos = view.selectionStart
        with(view.layout){
            val line = getLineForOffset(pos)
            val r = Rect()
            view.getGlobalVisibleRect(r)
            return r.left + getPrimaryHorizontal(pos).toInt() to r.top + getLineBaseline(line) + getLineAscent(line)
        }
    }
    fun addView(id:Int, view:View) = getActivity(view)?.let{it.findViewById<ViewGroup>(id).addView(view)}
}