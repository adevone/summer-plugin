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
class EventExactlyOnceIntention : AddToViewProxyIntention() {

    override fun getText(): String {
        return "doExactlyOnce"
    }

    override fun isPropertyCorrect(property: KtProperty): Boolean {
        return !property.isVar
    }

    override fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty {
        return factory.createDoExactlyOnceEvent(property)
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/EventOnlyWhenAttachedIntention"
    }
}
