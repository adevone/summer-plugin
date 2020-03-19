package ru.napoleonit.summerPlugin

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.psi.*

/**
 * Implements an intention action to replace a ternary statement with if-then-else
 *
 * @author dsl
 */
@NonNls
class AddMissingViewProxyPropertiesIntention : PsiElementBaseIntentionAction(), IntentionAction, PriorityAction {

    override fun getText(): String {
        return "Add missing properties to viewProxy"
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/AddMissingViewProxyPropertiesIntention"
    }

    override fun isAvailable(
        project: Project,
        editor: Editor?,
        element: PsiElement
    ): Boolean {
        val leafElement = element as? LeafPsiElement ?: return false
        val objectDeclaration = leafElement.parent as? KtObjectDeclaration ?: return false
        val objectLiteral = objectDeclaration.parent as? KtObjectLiteralExpression ?: return false
        val parentProp = objectLiteral.parent as? KtProperty ?: return false
        return parentProp.name == "viewProxy"
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {

        val leafElement = element as LeafPsiElement
        val objectDeclaration = leafElement.parent as KtObjectDeclaration
        val viewStateProxyObjectBody = objectDeclaration.body!!

        val stateInterface = element.containingFile.children
            .find {
                val clazz = (it as? KtClass)
                clazz?.let { ktClass ->
                    ktClass.isInterface() && ktClass.name?.contains("View") == true
                } == true
            } as KtClass

        val proxyProperties = stateInterface.body!!.children
            .mapNotNull {
                (it as? KtProperty)
            }

        if (viewStateProxyObjectBody.children.isEmpty()) {
            viewStateProxyObjectBody.clearFromWhitespaces()
        }

        val factory = KtPsiFactory(project)
        proxyProperties.forEach { property ->
            val propertyName = property.name!!
            if (!viewStateProxyObjectBody.containsPropertyWithName(propertyName)) {
                if (property.isVar) {
                    val newProperty = factory.createStateProperty(property)
                    viewStateProxyObjectBody.addBefore(newProperty, viewStateProxyObjectBody.lastChild)
                } else {
                    val newProperty = factory.createDoOnlyWhenAttachedEvent(property)
                    viewStateProxyObjectBody.addBefore(newProperty, viewStateProxyObjectBody.lastChild)
                }
            }
        }
    }

    override fun startInWriteAction(): Boolean = true

    override fun getPriority(): PriorityAction.Priority = PriorityAction.Priority.TOP
}
