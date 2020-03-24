package io.adev.summer.plugin

import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

/**
 * Implements an intention action to replace a ternary statement with if-then-else
 *
 * @author dsl
 */
@NonNls
class EventOnlyWhenAttachedIntention : AddToViewProxyIntention() {

    override fun getText(): String {
        return "doOnlyWhenAttached"
    }

    override fun isPropertyCorrect(property: KtProperty): Boolean {
        return !property.isVar
    }

    override fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty {
        return factory.createDoOnlyWhenAttachedEvent(property)
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/EventOnlyWhenAttachedIntention"
    }
}
