package io.adev.summer.plugin

import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.KtProperty

@NonNls
class AndroidEventImplementationIntention : EventImplementationIntention() {

    override fun getText(): String {
        return "Copy Android implementation"
    }

    override fun formatEventImplementation(property: KtProperty, params: List<EventParam>): String {
        return """
override val ${property.name}: ${formatType(params)} = { ${formatParamNames(params)}
    
}
        """.trim()
    }

    private fun formatType(params: List<EventParam>): String {
        return "(${params.joinToString { it.typeName }}) -> Unit"
    }

    private fun formatParamNames(params: List<EventParam>): String {
        return params.joinToString { it.name } + if (params.isNotEmpty()) " ->" else ""
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/AndroidEventImplementationIntention"
    }
}
