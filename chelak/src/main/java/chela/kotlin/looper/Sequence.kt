package chela.kotlin.looper

class Sequence internal constructor(private val looper: ChLooper){
    internal lateinit var item: ChItem

    infix fun next(block: ItemDSL.()->Unit): Sequence {
        val i = looper.getItem(ItemDSL().apply{block()})
        item.next = i
        item = i
        return this
    }
    operator fun plus(block: ItemDSL.()->Unit): Sequence {
        val i = looper.getItem(ItemDSL().apply{block()})
        item.next = i
        item = i
        return this
    }
}