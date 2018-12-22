package chela.kotlin.core

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

object ChReflect{
    class Item(val getter: Map<String, KProperty1.Getter<*, *>>, val setter:Map<String, KMutableProperty.Setter<*>>)
    @JvmStatic private val fields = mutableMapOf<KClass<*>, Item>()
    @JvmStatic fun fields(cls: KClass<*>):Item{
        if(fields[cls] != null) return fields[cls]!!
        val getter = mutableMapOf<String, KProperty1.Getter<*, *>>()
        val setters = mutableMapOf<String, KMutableProperty.Setter<*>>()
        cls.memberProperties.forEach{
            getter[it.name] = it.getter
            if(it is KMutableProperty<*>) setters[it.name] = it.setter
        }
        val item = Item(getter, setters)
        this.fields[cls] = item
        return item
    }
}