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

        val factory = KtPsiFactory(project)

        val fileObjectDeclarations = findObjectDeclarations(property.containingFile.children)
        var viewObjectDeclaration = fileObjectDeclarations.find { obj ->
            obj.getSuperTypeList()?.entries?.any { entry ->
                entry.typeAsUserType?.referencedName == viewInterface.name
            } == true
        }

        if (viewObjectDeclaration == null) {

            val viewModelClass = property.containingFile.children
                .find {
                    (it as? KtClass)?.name?.contains("ViewModel") == true
                } as KtClass

            val viewModelClassBody = viewModelClass.body
            var viewProxyProp = viewModelClassBody!!.viewProxyProperty()

            if (viewProxyProp == null) {
                viewProxyProp = factory.createProperty("override val viewProxy = object : ${viewInterface.name} {}")
                viewModelClassBody.addAfter(viewProxyProp, viewModelClassBody.lBrace)
                viewProxyProp = viewModelClassBody.viewProxyProperty()!!
                viewModelClassBody.addBefore(factory.createNewLine(2), viewProxyProp)
                viewModelClassBody.addAfter(factory.createNewLine(1), viewProxyProp)
            }

            val viewStateProxyObject = viewProxyProp.children
                    .find {
                        (it is KtObjectLiteralExpression)
                    } as KtObjectLiteralExpression

            viewObjectDeclaration = viewStateProxyObject.objectDeclaration
        }

        val viewProxyObjectBody = viewObjectDeclaration.children
            .find {
                it is KtClassBody
            } as KtClassBody

        if (viewProxyObjectBody.containsPropertyWithName(propertyName)) return

        if (viewProxyObjectBody.children.isEmpty()) {
            viewProxyObjectBody.clearFromWhitespaces()
        }

        val viewProxyPropertyNames = viewProxyObjectBody.children.mapNotNull { vpElement ->
            val vpProperty = vpElement as? KtProperty
            vpProperty?.name
        }

        var interfacePropertyAbove: KtProperty? = null
        for (iElement in body.children) {
            val interfaceProperty = iElement as? KtProperty
            if (interfaceProperty != null) {
                val interfacePropertyName = interfaceProperty.name
                if (interfacePropertyName == property.name) {
                    break
                } else {
                    if (interfacePropertyName in viewProxyPropertyNames) {
                        interfacePropertyAbove = interfaceProperty
                    }
                }
            }
        }

        val viewProxyPropertyAbove = if (interfacePropertyAbove != null) {
            viewProxyObjectBody.children.find { vpElement ->
                val viewProxyProperty = vpElement as? KtProperty
                viewProxyProperty?.name == interfacePropertyAbove?.name
            }
        } else {
            null
        }

        val newProperty = createProperty(factory, property)
        viewProxyObjectBody.addAfter(
            newProperty,
            viewProxyPropertyAbove ?: viewProxyObjectBody.firstChild
        )
    }

    private fun KtClassBody.viewProxyProperty(): KtProperty? {
        return this.children
            .filterIsInstance<KtProperty>()
            .find { viewModelProperty ->
                viewModelProperty.name == "viewProxy"
            }
    }

    private fun findObjectDeclarations(elements: Array<PsiElement>): List<KtObjectDeclaration> {
        return elements.flatMap { element ->
            val obj = element as? KtObjectDeclaration
            val children = findObjectDeclarations(element.children)
            if (obj != null)
                children + obj
            else
                children
        }
    }

    protected abstract fun createProperty(factory: KtPsiFactory, property: KtProperty): KtProperty

    override fun startInWriteAction(): Boolean = true

    override fun getPriority(): PriorityAction.Priority = PriorityAction.Priority.TOP
}
