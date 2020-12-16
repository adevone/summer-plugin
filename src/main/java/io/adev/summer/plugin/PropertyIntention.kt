package io.adev.summer.plugin

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassBody
import org.jetbrains.kotlin.psi.KtProperty

abstract class PropertyIntention : PsiElementBaseIntentionAction(), IntentionAction, PriorityAction {

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        element: PsiElement
    ): Boolean {
        val identifier = element as? LeafPsiElement ?: return false
        val property = identifier.parent as? KtProperty ?: return false
        val body = property.parent as? KtClassBody ?: return false
        val interfaze = body.parent as? KtClass
        return interfaze != null && isPropertyCorrect(property)
    }

    abstract fun isPropertyCorrect(property: KtProperty): Boolean

    override fun startInWriteAction(): Boolean = true
}
