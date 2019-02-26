package chela.kotlin.looper

class ItemDSL{
    companion object{val empty: ItemBlock = {}}
    var time = -1
    var delay = 0
    var loop = 1
    var block: ItemBlock = empty
    var ended: ItemBlock = empty
    var isInfinity = false
}