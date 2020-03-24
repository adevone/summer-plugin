package io.adev.summer.plugin

import com.intellij.codeInsight.intention.PriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.util.IncorrectOperationException
import org.jetbrains.kotlin.psi.*

abstract class AddToViewProxyIntention : PropertyIntention() {

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {

        val property = element.parent as KtProperty
        val propertyName = property.text
        val body = property.parent as? KtClassBody ?: return
        val viewInterface = body.parent as? KtClass ?: return

        val presenterClass = property.containingFile.children
            .find {
                (it as? KtClass)?.name?.contains("Presenter") == true
            } as KtClass

        val presenterClassBody = presenterClass.body
        var viewProxyProp = presenterClassBody!!.viewProxyProperty()

        val factory = KtPsiFactory(project)

        if (viewProxyProp == null) {
            viewProxyProp = factory.createProperty("override val viewProxy = object : ${viewInterface.name} {}")
            presenterClassBody.addAfter(viewProxyProp, presenterClassBody.lBrace)
            viewProxyProp = presenterClassBody.viewProxyProperty()!!
            presenterClassBody.addBefore(factory.createNewLine(2), viewProxyProp)
            presenterClassBody.addAfter(factory.createNewLine(1), viewProxyProp)
        }

        val viewStateProxyObject = viewProxyProp.children
            .find {
                (it is KtObjectLiteralExpression)
            } as KtObjectLiteralExpression

        val viewProxyObjectBody = viewStateProxyObject.objectDeclaration.children
            .find {
                it is KtClassBody
            } as KtClassBody

        if (viewProxyObjectBody.containsPropertyWithName(propertyName)) return

        if (viewProxyObjectBody.children.isEmpty()) {
            viewProxyObjectBody.clearFromWhitespaces()
        }

        val newProperty = createProperty(factory, property)
        viewProxyObjectBody.addBefore(newProperty, viewProxyObjectBody.lastChild)
    }

    private fun KtClassBody.viewProxyProperty(): KtProperty? {
        return this.children
            .filterIsInstance<KtProperty>()
            .find { presenterProperty ->
                presenterProperty.name == "viewProxy"
            }
    }

    protected abstract fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty

    override fun startInWriteAction(): Boolean = true

    override fun getPriority(): PriorityAction.Priority = PriorityAction.Priority.TOP
}
