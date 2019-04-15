package chela.kotlin.android

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.thread.ChThread

object ChKeyboard{
    fun hide(act:AppCompatActivity){
        ChThread.main(100, Runnable{
            ChApp.imm.hideSoftInputFromWindow(
                act.window.decorView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        })
    }
    fun show() = ChApp.imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    fun show(et:EditText) = ChApp.imm.showSoftInput(et, 0)
}