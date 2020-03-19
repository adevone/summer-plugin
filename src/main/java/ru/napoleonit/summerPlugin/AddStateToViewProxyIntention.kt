package ru.napoleonit.summerPlugin

import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Implements an intention action to replace a ternary statement with if-then-else
 *
 * @author dsl
 */
@NonNls
class AddStateToViewProxyIntention : AddToViewProxyIntention() {

    override fun getText(): String {
        return "state"
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/AddStateToViewProxyIntention"
    }

    override fun isPropertyCorrect(property: KtProperty): Boolean {
        return property.isVar
    }

    override fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty {
        return factory.createStateProperty(property)
    }
}
