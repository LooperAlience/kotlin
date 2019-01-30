package com.chela.annotation

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic.Kind.ERROR


@Suppress("INCOMPATIBLE_ENUM_COMPARISON")
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions("kapt.kotlin.generated")
class ChProcessor : AbstractProcessor(){
    val exProp = "ref,isSet,OBJECT,ARRAY,isTypeChecked,Companion,INSTANCE".split(",")
    val exMethod = "getClass,hashCode,clone,toString,notify,notifyAll,equals,wait,finalize".split(",")
    private fun members(e:Element)=
        processingEnv.elementUtils.getAllMembers(processingEnv.typeUtils.asElement(e.asType()) as TypeElement)

    override fun getSupportedAnnotationTypes() = mutableSetOf(
        STYLE::class.java.name
    )
    override fun getSupportedSourceVersion() = SourceVersion.latest()!!
    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment:RoundEnvironment?):Boolean{
        roundEnvironment?.getElementsAnnotatedWith(STYLE::class.java)?.forEach{
            val methods = ElementFilter.methodsIn(members(it))
            val prop = ElementFilter.fieldsIn(members(it))
            val annotated = mutableListOf<String>()
            val getter = mutableListOf<String>()
            val fields = mutableListOf<String>()
            methods.forEach{
                val name = "${it.simpleName}"
                if(name != "get" && name.startsWith("get") &&  !exMethod.contains(name)){
                    getter += "${name[3]}".toLowerCase() + name.substring(4)
                }else if(it.getAnnotation(Ex::class.java) != null){
                    annotated += name.replace("$" + "annotations", "")
                }
            }
            getter.forEach{if(!annotated.contains(it)) fields += it}
            prop.any{
                val name = "${it.simpleName}"
                if(!fields.contains(name) && !exProp.contains(name) && !annotated.contains(name)){
                    fields += name
                }
                false
            }
            processingEnv.messager.printMessage(ERROR, "$fields")
        }

        return false
    }

}
