/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wix.rt.actions;

import com.intellij.ide.actions.TemplateKindCombo;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.lang.javascript.JavaScriptFileType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import com.wix.rt.RTBundle;
import com.wix.rt.RTProjectComponent;
import com.wix.rt.build.RTFileType;
import com.wix.rt.settings.Settings;
import icons.RTIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.io.IOException;

/**
 * @author yole
 */
public class CreateRTAction extends AbstractCreateFormAction {
    private static final Logger LOG = Logger.getInstance("#com.wix.rt.actions.CreateFormAction");

    public CreateRTAction() {
        super(RTBundle.message("action.gui.rt.text"), RTBundle.message("action.gui.rt.description"), RTIcons.RT);

        // delete obsolete template
        ApplicationManager.getApplication().executeOnPooledThread(new Runnable() {
            public void run() {
                // to prevent deadlocks, this code must run while not holding the ActionManager lock
                FileTemplateManager manager = FileTemplateManager.getInstance();
                final FileTemplate template = manager.getTemplate("RTFile3");
                //noinspection HardCodedStringLiteral
                if (template != null && template.getExtension().equals("rt")) {
                    manager.removeTemplate(template);
                }
            }
        });
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        e.getPresentation().setVisible(RTActionUtil.isRTEnabled(project));
    }

    @NotNull
    protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
        final MyInputValidator validator = new MyInputValidator(project, directory);
        final MyDialog dialog = new MyDialog(project, validator);
        dialog.show();
        createController = dialog.shouldCreateController();
        return validator.getCreatedElements();
    }

    private boolean createController = true;

    private static String getControllerTemplate(String name, String modules) {
        String s = "";
        try {
            s = FileUtil.loadTextAndClose(CreateRTAction.class.getResourceAsStream("/fileTemplates/internal/RT Controller File " + modules + ".js.ft"));
            s = StringUtil.replace(s, "$name$", name);
        } catch (IOException e) {
//      throw new IncorrectOperationException(RTBundle.message("error.cannot.read", formName), (Throwable)e);
        }
        return StringUtil.convertLineSeparators(s);
    }

    private static String getTemplate() {
        String s = "";
        try {
            s = FileUtil.loadTextAndClose(CreateRTAction.class.getResourceAsStream("/fileTemplates/internal/RT File.rt.ft"));
        } catch (IOException e) {
//      throw new IncorrectOperationException(RTBundle.message("error.cannot.read", formName), (Throwable)e);
        }
        return StringUtil.convertLineSeparators(s);
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        PsiElement createdFile;
        PsiElement createdControllerFile;
        try {
            final Project project = directory.getProject();
            final String formBody = getTemplate();
            @NonNls final String fileName = newName + ".rt";
            final PsiFile formFile = PsiFileFactory.getInstance(project).createFileFromText(fileName, RTFileType.INSTANCE, formBody);
            createdFile = directory.add(formFile);
            if (createController) {
                // TODO generate according to selected modules
                final String controllerFileName = newName + ".js";
                String modules = Settings.getInstance(project).modules;
                final String controllerBody = getControllerTemplate(newName, modules);
                final PsiFile controllerFile = PsiFileFactory.getInstance(project).createFileFromText(controllerFileName, JavaScriptFileType.INSTANCE, controllerBody);
                createdControllerFile = directory.add(controllerFile);
                return new PsiElement[]{createdFile, createdControllerFile};
            }
            return new PsiElement[]{createdFile};
        } catch (IncorrectOperationException e) {
            throw e;
        } catch (Exception e) {
            LOG.error(e);
            return PsiElement.EMPTY_ARRAY;
        }
    }

    protected String getErrorTitle() {
        return RTBundle.message("error.cannot.create.rt");
    }

    protected String getCommandName() {
        return RTBundle.message("command.create.rt");
    }

    private class MyDialog extends DialogWrapper {
        private JPanel myTopPanel;
        private JTextField myFormNameTextField;
        private TemplateKindCombo myBaseLayoutManagerCombo;
        private JLabel myUpDownHintForm;
        private JCheckBox createControllerCheckBox;

        private final Project myProject;
        private final MyInputValidator myValidator;

        public boolean shouldCreateController() {
            return createControllerCheckBox.isSelected();
        }

        public MyDialog(final Project project, final MyInputValidator validator) {
            super(project, true);
            myProject = project;
            myValidator = validator;
            myBaseLayoutManagerCombo.registerUpDownHint(myFormNameTextField);
            myUpDownHintForm.setIcon(PlatformIcons.UP_DOWN_ARROWS);
            myBaseLayoutManagerCombo.addItem("None", RTIcons.RT, "none");
            myBaseLayoutManagerCombo.addItem("AMD", RTIcons.RT, "amd");
            myBaseLayoutManagerCombo.addItem("CommonJS", RTIcons.RT, "commonjs");
            myBaseLayoutManagerCombo.addItem("ES6", RTIcons.RT, "es6");
            myBaseLayoutManagerCombo.addItem("Typescript", RTIcons.RT, "typescript");
            init();
            setTitle(RTBundle.message("title.new.gui.form"));
            setOKActionEnabled(false);

            myFormNameTextField.getDocument().addDocumentListener(new DocumentAdapter() {
                protected void textChanged(DocumentEvent e) {
                    setOKActionEnabled(!myFormNameTextField.getText().isEmpty());
                }
            });

            myBaseLayoutManagerCombo.setSelectedName(Settings.getInstance(project).modules);
            myBaseLayoutManagerCombo.setEnabled(false);
//            myBaseLayoutManagerCombo.setSelectedName(GuiDesignerConfiguration.getInstance(project).DEFAULT_LAYOUT_MANAGER);
        }

        protected JComponent createCenterPanel() {
            return myTopPanel;
        }

        protected void doOKAction() {
            final String inputString = myFormNameTextField.getText().trim();
            if (myValidator.checkInput(inputString) && myValidator.canClose(inputString)) {
                close(OK_EXIT_CODE);
            }
            close(OK_EXIT_CODE);
        }

        public JComponent getPreferredFocusedComponent() {
            return myFormNameTextField;
        }
    }
}
