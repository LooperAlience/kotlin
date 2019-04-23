package chela.kotlin.core

import chela.kotlin.view.ChStyleModel
import chela.kotlin.view.ChViewModel
import com.chela.annotation.EX
import com.chela.annotation.PROP
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object ChReflect{
    class Item(
        val getter:Map<String, KProperty1.Getter<*, *>>,
        val setter:Map<String, KMutableProperty.Setter<*>>,
        val annotation:Map<String, String>
    )
    private val fields = mutableMapOf<KClass<*>, Item>()
    fun fields(cls: KClass<*>):Item{
        if(fields[cls] != null) return fields[cls]!!
        val getter = mutableMapOf<String, KProperty1.Getter<*, *>>()
        val setters = mutableMapOf<String, KMutableProperty.Setter<*>>()
        val anno = mutableMapOf<String, String>()
        cls.memberProperties.forEach{
            val name = it.name
            if(cls === ChStyleModel::class && it.findAnnotation<EX>() != null) anno[name] = "EX"
            else if(cls === ChViewModel::class && it.findAnnotation<PROP>() != null) anno[name] = "PROP"
            getter[name] = it.getter
            if(it is KMutableProperty<*>) setters[name] = it.setter
        }
        val item = Item(getter, setters, anno)
        this.fields[cls] = item
        return item
    }
}