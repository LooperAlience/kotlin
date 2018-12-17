package chela.kotlin.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

val NONE = object{}

val reflectFields = mutableMapOf<KClass<*>, Map<String, KProperty1.Getter<*, *>>>()
fun reflectFields(cls: KClass<*>):Map<String, KProperty1.Getter<*, *>>{
    if(reflectFields[cls] != null) return reflectFields[cls]!!
    val fields = mutableMapOf<String, KProperty1.Getter<*, *>>()
    cls.memberProperties.forEach{fields[it.name] = it.getter}
    reflectFields[cls] = fields
    return fields
}
fun reflectField(target:Any, name:String):Any = reflectFields[target::class]?.get(name)?.call(target) ?: NONE