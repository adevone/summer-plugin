package ru.napoleonit.summerPlugin

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.psi.*

abstract class AddToViewProxyIntention : PsiElementBaseIntentionAction(), IntentionAction, PriorityAction {

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        element: PsiElement
    ): Boolean {
        val identifier = element as? LeafPsiElement ?: return false
        val property = identifier.parent as? KtProperty ?: return false
        val body = property.parent as? KtClassBody ?: return false
        val interfaze = body.parent as? KtClass ?: return false
        return interfaze.name?.endsWith("View") == true && isPropertyCorrect(property)
    }

    abstract fun isPropertyCorrect(property: KtProperty): Boolean

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {

        val property = element.parent as KtProperty
        val propertyName = property.text

        val presenterClass = property.containingFile.children
            .find {
                (it as? KtClass)?.name?.contains("Presenter") == true
            } as KtClass

        val presenterClassBody = presenterClass.body
        val viewStateProxyProp = presenterClassBody!!.children
            .filterIsInstance<KtProperty>()
            .find { presenterProperty ->
                presenterProperty.name == "viewProxy"
            } ?: return

        val viewStateProxyObject = viewStateProxyProp.children
            .find {
                (it is KtObjectLiteralExpression)
            } as KtObjectLiteralExpression

        val viewStateProxyObjectBody = viewStateProxyObject.objectDeclaration.children
            .find {
                it is KtClassBody
            } as KtClassBody

        if (viewStateProxyObjectBody.containsPropertyWithName(propertyName)) return

        if (viewStateProxyObjectBody.children.isEmpty()) {
            viewStateProxyObjectBody.clearFromWhitespaces()
        }

        val factory = KtPsiFactory(project)
        val newProperty = createProperty(factory, property)
        viewStateProxyObjectBody.addBefore(newProperty, viewStateProxyObjectBody.lastChild)
    }

    protected abstract fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty

    override fun startInWriteAction(): Boolean = true

    override fun getPriority(): PriorityAction.Priority = PriorityAction.Priority.TOP
}
