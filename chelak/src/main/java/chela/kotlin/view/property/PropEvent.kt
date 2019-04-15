package chela.kotlin.view.property

import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import chela.kotlin.Ch
import java.util.*

/**
 * Base object for view event.
 */
object PropEvent:Property(){
    fun click(view: View, v:Any){
        if(v !is View.OnClickListener) return
        view.isClickable = true
        view.setOnClickListener(v)
    }
    fun longClick(view: View, v:Any){
        if(v !is View.OnLongClickListener) return
        view.isLongClickable = true
        view.setOnLongClickListener(v)
    }
    fun clickable(view: View, v:Any){
        if(v !is Boolean) return
        view.isClickable = true
    }
    fun longClickable(view: View, v:Any){
        if(v !is Boolean) return
        view.isLongClickable = true
    }
    fun focusChange(view:View, v:Any){
        if(v !is View.OnFocusChangeListener || view !is EditText) return
        view.setOnFocusChangeListener(v)
    }
    fun focusable(view:View, v:Any){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            view.focusable = when(v){
                is Boolean->if(v) View.FOCUSABLE else View.NOT_FOCUSABLE
                is Number-> v.toInt()
                else -> View.FOCUSABLE
            }
        }
    }
    fun focusableInTouchMode(view:View, v:Any){
        if(v !is Boolean) return
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            view.isFocusableInTouchMode = v
        }
    }
    fun focus(view:View, v:Any){
        if(v !is Boolean) return
        focusable(view, v)
        if(v) view.requestFocus()
        focusableInTouchMode(view, v)
    }
    fun textChanged(view: View, v:Any){
        if(v !is Ch.OnTextChanged || view !is EditText) return
        v.text = view
        view.addTextChangedListener(v)
    }

    fun editorAction(view: View, v:Any){
        if(v !is TextView.OnEditorActionListener || view !is EditText) return
        view.setOnEditorActionListener(v)
    }

    private val touches = WeakHashMap<View, MutableMap<String, Ch.Touch>>()
    private val hasTouch = WeakHashMap<View, Boolean>()
    /**
     * @param view Attach to the view if there is no touch listener.
     * @param v Only works if its type is Ch.Touch.
     * @return MutableMap contains MotionEvent type as key and interface as value.
     */
    private fun touch(view:View, v:Any):MutableMap<String, Ch.Touch>?{
        if(v !is Ch.Touch) return null
        if(hasTouch[view] == null){
            hasTouch[view] = true
            view.setOnTouchListener{_, e->
                return@setOnTouchListener touches[view]?.let {
                    when(e.action){
                        MotionEvent.ACTION_DOWN->it["down"]?.onTouch(e)
                        MotionEvent.ACTION_UP-> it["up"]?.onTouch(e)
                        MotionEvent.ACTION_MOVE-> it["move"]?.onTouch(e)
                        else->true
                    }
                } ?: true
            }
            touches[view] = mutableMapOf()
        }
        return touches[view]
    }
    fun down(view:View, v:Any) = touch(view, v)?.put("down", v as Ch.Touch)
    fun up(view:View, v:Any) = touch(view, v)?.put("up", v as Ch.Touch)
    fun move(view:View, v:Any) = touch(view, v)?.put("move", v as Ch.Touch)
}