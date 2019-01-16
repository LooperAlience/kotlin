package chela.kotlin.view

import chela.kotlin.view.ChStyle.items

/**
 * This object cached style property on [items].
 */
object ChStyle{
    @JvmStatic val items = mutableMapOf<String, Map<String, Any>>()
    @JvmStatic fun add(k:String, map:Map<String, Any>){items[k] = map}
    @JvmStatic fun remove(k:String) = items.remove(k)
    @JvmStatic operator fun get(k:String):Map<String, Any>? = items[k]
}