package chela.androidTest.holder

import android.view.View
import chela.androidTest.R
import chela.androidTest.groupBase
import chela.androidTest.looper
import chela.androidTest.router
import chela.androidTest.viewmodel.MainVM
import chela.kotlin.Ch
import chela.kotlin.looper.ChLooper.Item.Ended
import chela.kotlin.looper.ChLooper.Item.Time
import chela.kotlin.viewmodel.holder.ChGroupBase
import chela.kotlin.viewmodel.holder.ChHolder
import chela.kotlin.viewmodel.holder.ChHolderBase

object MainHD: ChHolder<View>(){
    private val main = MainVM

    override fun create(base: ChHolderBase<View>): View{
        if(base !is ChGroupBase) throw Exception("invalid base:$base")
        return Ch.scanner.scan(this, base.inflate(R.layout.main)).render()
    }
    override fun push(base: ChHolderBase<View>, isRestore:Boolean){
        main.pushed(groupBase.group.width.toDouble())
        if(isRestore){
            router.unlockPush()
            main.x = 0.0
            Ch.scanner[this]?.render()
        }else{
            looper.invoke(Time(700)) {
                main.x = it.circleOut(main.width, 0.0)
                Ch.scanner[this]?.renderSync()
            }.next(Time(700), Ended{router.unlockPush()}) {
                main.fontSize = it.bounceOut(15.0, 60.0)
                Ch.scanner[this]?.renderSync()
            }
        }
        if(main.isSet) return
        Ch.net("GET", "https://www.bsidesoft.com/hika/chela/test.json")
        .send{data, _, _->
            data?.let{
                main.fromJson(it)
                Ch.scanner[this]?.render()
            }
        }
    }
}