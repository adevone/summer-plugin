package io.adev.summer.plugin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.nj2k.postProcessing.type
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

fun KtPsiFactory.createStateProperty(property: KtProperty): KtProperty {
    val name = property.name!!
    val typeName = property.type().toString()
    val initialValue = when (typeName) {
        "String" -> "\"\""
        "Char" -> "''"
        "Int" -> "0"
        "Long" -> "0L"
        "Short" -> "0"
        "Float" -> "0f"
        "Double" -> "0.0"
        "Boolean" -> "false"
        else -> "null"
    }
    return createProperty("override var $name by state({ it::$name }, initial = $initialValue)")
}

fun KtPsiFactory.createDoOnlyWhenAttachedEvent(property: KtProperty): KtProperty {
    val name = property.name!!
    return createProperty("override val $name = event { it.$name }.doOnlyWhenAttached()")
}

fun KtPsiFactory.createDoExactlyOnceEvent(property: KtProperty): KtProperty {
    val name = property.name!!
    return createProperty("override val $name = event { it.$name }.doExactlyOnce()")
}

fun KtClassBody.containsPropertyWithName(name: String) =
    children.any { it is KtProperty && it.name == name }

fun KtClassBody.clearFromWhitespaces() {
    var whiteSpace: PsiElement? = this.getChildOfType<PsiWhiteSpace>()
    while (whiteSpace != null) {
        this.deleteChildInternal(whiteSpace.node)
        whiteSpace = this.getChildOfType<PsiWhiteSpace>()
    }
}