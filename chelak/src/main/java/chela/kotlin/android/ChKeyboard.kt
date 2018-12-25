package chela.kotlin.android

import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import chela.kotlin.Ch

object ChKeyboard{
    @JvmStatic fun hide(act:AppCompatActivity){
        Ch.thread.main(100, Runnable{
            Ch.app.imm.hideSoftInputFromWindow(
                act.window.decorView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        })
    }
    @JvmStatic fun show() = Ch.app.imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    @JvmStatic fun show(et:EditText) = Ch.app.imm.showSoftInput(et, 0)
}