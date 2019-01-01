package chela.kotlin.view.router.holder

import androidx.fragment.app.Fragment

data class ChFragmentInfo(
    val fragment: Fragment,
    val tag:String? = null,
    val backStack:String? = null,
    val transition:Int = 0,
    val style:Int = 0,
    val animation:List<Int> = listOf()
)