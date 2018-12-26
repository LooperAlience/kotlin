package chela.kotlin.viewmodel.holder

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class ChFragmentBase: ChHolderBase<Fragment>(){
    lateinit var manager:FragmentManager
    
    override fun push(holder: ChHolder<Fragment>){}
    override fun pop(holder: ChHolder<Fragment>){}
}