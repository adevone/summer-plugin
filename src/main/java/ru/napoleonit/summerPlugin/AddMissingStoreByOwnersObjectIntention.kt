package ru.napoleonit.summerPlugin

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.diagnostic.Logger
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
class AddMissingStoreByOwnersObjectIntention : PsiElementBaseIntentionAction(), IntentionAction, PriorityAction {

    private val logger = Logger.getInstance("StoreByOwnerPropertyIntention")

    override fun getText(): String {
        return "Store missing properties"
    }

    override fun getFamilyName(): String {
        return "intentionDescriptions/AddMissingStoreByOwnersObjectIntention"
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
        return parentProp.name == "viewStateProxy"
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {

        val leafElement = element as LeafPsiElement
        val objectDeclaration = leafElement.parent as KtObjectDeclaration
        val viewStateProxyObjectBody = objectDeclaration.body!!

        val viewObject = element.containingFile.children
            .find {
                (it as? KtObjectDeclaration)?.name?.contains("View") == true
            } as KtObjectDeclaration

        val stateInterface = viewObject.body!!.children
            .find {
                (it as? KtClass)?.let { ktClass -> ktClass.isInterface() && ktClass.name == "State" } == true
            } as KtClass

        val stateProperties = stateInterface.body!!.children
            .mapNotNull {
                (it as? KtProperty)
            }

        if (viewStateProxyObjectBody.children.isEmpty()) {
            viewStateProxyObjectBody.clearFromWhitespaces()
        }

        val factory = KtPsiFactory(project)
        stateProperties.forEach { stateProperty ->
            val propertyName = stateProperty.name!!
            if (!viewStateProxyObjectBody.containsPropertyWithName(propertyName)) {
                val newProperty = factory.createStoreByOwnerProperty(propertyName)
                viewStateProxyObjectBody.addBefore(newProperty, viewStateProxyObjectBody.lastChild)
            }
        }
    }

    override fun startInWriteAction(): Boolean = true

    override fun getPriority(): PriorityAction.Priority = PriorityAction.Priority.TOP
}
