package io.adev.summer.plugin

import com.jetbrains.rd.util.firstOrNull
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtProperty

/**
 * Implements an intention action to replace a ternary statement with if-then-else
 *
 * @author dsl
 */
@NonNls
class IosEventImplementationIntention : CopyPropertyImplementationIntention() {

    override fun getText(): String {
        return "Copy iOS implementation"
    }

    private data class Param(
        val name: String,
        val typeName: String
    )

    override fun formatProperty(property: KtProperty): String {

        val type = property.type()!!
        val parameters = type.arguments.dropLast(1)

        val params = parameters.mapIndexed { index, parameter ->
            val name = parameter.type.annotations
                .findAnnotation(FqName("kotlin.ParameterName"))
                ?.allValueArguments?.firstOrNull()?.value?.value?.toString() ?: "param${index}"
            val typeName = parameter.type.constructor.toString()
            Param(name, typeName)
        }

        return """
lazy var ${property.name}: (${params.joinToString { it.typeName }}) -> Void = { ${params.joinToString { it.name }} in
    
}
        """.trim()
    }

    override fun isPropertyCorrect(property: KtProperty): Boolean {
        return !property.isVar
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/IosEventImplementationIntention"
    }
}
