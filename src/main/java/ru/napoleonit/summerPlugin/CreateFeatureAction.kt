package ru.napoleonit.summerPlugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.wizard.WizardDialog
import com.intellij.ui.wizard.WizardModel
import com.intellij.ui.wizard.WizardNavigationState
import com.intellij.ui.wizard.WizardStep
import javax.swing.JComponent
import javax.swing.JPanel

class CreateFeatureAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        CreateFeatureWizard(CreateFeatureWizard.Model()).show()
    }
}

class CreateFeatureWizard(
    private val model: Model
) : WizardDialog<CreateFeatureWizard.Model>(
    true,
    true,
    model
) {
    class Model : WizardModel("Create summer feature") {
        init {
            add(MainStep())
        }
    }

    class MainStep : WizardStep<Model>() {

        override fun prepare(state: WizardNavigationState?): JComponent {
            return JPanel()
        }
    }
}