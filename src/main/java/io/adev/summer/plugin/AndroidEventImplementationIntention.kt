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
override val ${property.name} = { ${params.joinToString { "${it.name}: ${it.typeName}" }} ->
    
}
        """.trim()
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/AndroidEventImplementationIntention"
    }
}
