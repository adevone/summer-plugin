package ru.napoleonit.summerPlugin

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType

fun KtPsiFactory.createStoreByOwnerProperty(name: String) =
    createProperty("override var $name by store(vs::$name, initialValue = null)")

fun KtClassBody.containsPropertyWithName(name: String) =
    children.any { it is KtProperty && it.name == name }

fun KtClassBody.clearFromWhitespaces() {
    var whiteSpace: PsiElement? = this.getChildOfType<PsiWhiteSpace>()
    while (whiteSpace != null) {
        this.deleteChildInternal(whiteSpace.node)
        whiteSpace = this.getChildOfType<PsiWhiteSpace>()
    }
}