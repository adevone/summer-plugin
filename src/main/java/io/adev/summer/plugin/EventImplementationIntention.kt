package io.adev.summer.plugin

import com.jetbrains.rd.util.firstOrNull
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtProperty

data class EventParam(
    val name: String,
    val typeName: String
)

@NonNls
abstract class EventImplementationIntention : CopyPropertyImplementationIntention() {

    override fun formatProperty(property: KtProperty): String {

        val type = property.type()!!
        val parameters = type.arguments.dropLast(1)

        val params = parameters.mapIndexed { index, parameter ->
            val name = parameter.type.annotations
                .findAnnotation(FqName("kotlin.ParameterName"))
                ?.allValueArguments?.firstOrNull()?.value?.value?.toString() ?: "param${index}"
            val typeName = parameter.type.constructor.toString()
            EventParam(name, typeName)
        }

        return formatEventImplementation(property, params)
    }

    abstract fun formatEventImplementation(property: KtProperty, params: List<EventParam>): String

    override fun isPropertyCorrect(property: KtProperty): Boolean {
        return !property.isVar
    }
}