package com.wix.rt.settings;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionException;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.javascript.nodejs.NodeDetectionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiManager;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.intellij.util.NotNullProducer;
import com.intellij.util.ui.UIUtil;
import com.intellij.webcore.ui.SwingHelper;
import com.wix.rt.RTProjectComponent;
import com.wix.rt.cli.RTFinder;
import com.wix.rtk.cli.RTRunner;
import com.wix.rtk.cli.RTSettings;
import com.wix.settings.ValidationInfo;
import com.wix.settings.ValidationUtils;
import com.wix.settings.Validator;
import com.wix.ui.PackagesNotificationPanel;
import com.wix.utils.FileUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.icons.AllIcons.Debugger.ThreadStates.Exception;
import static com.intellij.structuralsearch.impl.matcher.PatternTreeContext.File;
import static org.mozilla.javascript.TopLevel.Builtins.String;

public class RTSettingsPage implements Configurable {
    private static final String FIX_IT = "Fix it";
    private static final String HOW_TO_USE_RT = "How to Use React-Templates";
    private static final String HOW_TO_USE_LINK = "https://github.com/wix/react-templates";
    protected Project project;

    private JCheckBox pluginEnabledCheckbox;
    private JPanel panel;
    private JPanel errorPanel;
    private TextFieldWithHistoryWithBrowseButton rtBinField;
    private TextFieldWithHistoryWithBrowseButton nodeInterpreterField;
    private HyperlinkLabel usageLink;
    private JLabel pathToRTBinLabel;
    private JLabel nodeInterpreterLabel;
    private JLabel versionLabel;
    private JCheckBox groupController;
    private JRadioButton noneGlobalsRadioButton;
    private JRadioButton AMDRadioButton;
    private JRadioButton commonJSRadioButton;
    private JPanel modulesPanel;
    private JLabel modulesLabel;
    private JCheckBox groupOtherFilesCheckBox;
    private JRadioButton ES6RadioButton;
    private JRadioButton typescriptRadioButton;
    private JComboBox<String> comboBoxTargetVersion;
    private JLabel targetVersionLabel;
    private JCheckBox reactNativeCheckBox;
    private JCheckBox watchAndCompileRtCheckBox;
    private final PackagesNotificationPanel packagesNotificationPanel;

//    private String

    public RTSettingsPage(@NotNull final Project project) {
        this.project = project;
        configRTBinField();
        configNodeField();
        pluginEnabledCheckbox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                boolean enabled = e.getStateChange() == ItemEvent.SELECTED;
                setEnabledState(enabled);
                if (enabled) {
                    List<File> newFiles = NodeDetectionUtil.listAllPossibleNodeInterpreters();
                    if (!newFiles.isEmpty() && nodeInterpreterField.getChildComponent().getText().isEmpty()) {
                        nodeInterpreterField.getChildComponent().setText(newFiles.get(0).getAbsolutePath());
                    }
                    List<File> rtBinFiles = RTFinder.searchForRTBin(getProjectPath());
                    if (!rtBinFiles.isEmpty() && rtBinField.getChildComponent().getText().isEmpty()) {
                        rtBinField.getChildComponent().setText(rtBinFiles.get(0).getAbsolutePath());
                    }
                }
            }
        });

//        System.out.println(String.format("", ""));

        this.packagesNotificationPanel = new PackagesNotificationPanel(project);
//        GridConstraints gridConstraints = new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
//                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
//                null, new Dimension(250, 150), null);
        errorPanel.add(this.packagesNotificationPanel.getComponent(), BorderLayout.CENTER);

        DocumentAdapter docAdp = new DocumentAdapter() {
            protected void textChanged(DocumentEvent e) {
                updateLaterInEDT();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                super.changedUpdate(e);
//                System.out.println("changedUpdate " + rtBinField.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                super.insertUpdate(e);
//                System.out.println("insertUpdate " + rtBinField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
//                super.removeUpdate(e);
//                e.getDocument().
                System.out.println("removeUpdate " + rtBinField.getText());
            }
        };

//        rtBinField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
//        nodeInterpreterField.getChildComponent().getTextEditor().getDocument().addDocumentListener(docAdp);
        rtBinField.getChildComponent().addDocumentListener(docAdp);
        nodeInterpreterField.getChildComponent().addDocumentListener(docAdp);
    }

    private File getProjectPath() {
        return new File(project.getBaseDir().getPath());
    }

    private void updateLaterInEDT() {
        UIUtil.invokeLaterIfNeeded(new Runnable() {
            public void run() {
                RTSettingsPage.this.update();
            }
        });
    }

    private void update() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        validate();
    }

    private String getModules() {
        if (AMDRadioButton.isSelected()) {
            return RTRunner.AMD;
        }
        if (commonJSRadioButton.isSelected()) {
            return RTRunner.COMMONJS;
        }
        if (ES6RadioButton.isSelected()) {
            return RTRunner.ES6;
        }
        if (typescriptRadioButton.isSelected()) {
            return RTRunner.TYPESCRIPT;
        }
        return RTRunner.NONE;
    }

    private void setEnabledState(boolean enabled) {
        rtBinField.setEnabled(enabled);
        nodeInterpreterField.setEnabled(enabled);
        pathToRTBinLabel.setEnabled(enabled);
        nodeInterpreterLabel.setEnabled(enabled);
        modulesPanel.setEnabled(enabled);
        groupController.setEnabled(enabled);
        commonJSRadioButton.setEnabled(enabled);
        noneGlobalsRadioButton.setEnabled(enabled);
        AMDRadioButton.setEnabled(enabled);
        ES6RadioButton.setEnabled(enabled);
        typescriptRadioButton.setEnabled(enabled);
        comboBoxTargetVersion.setEnabled(enabled);
        targetVersionLabel.setEnabled(enabled);
        modulesLabel.setEnabled(enabled);
        reactNativeCheckBox.setEnabled(enabled);
        watchAndCompileRtCheckBox.setEnabled(enabled);
        groupOtherFilesCheckBox.setEnabled(enabled);
    }

    private void validateField(Validator validator, TextFieldWithHistoryWithBrowseButton field, boolean allowEmpty, String message) {
        if (!ValidationUtils.validatePath(project, field.getChildComponent().getText(), allowEmpty)) {
            ValidationInfo error = new ValidationInfo(field.getChildComponent().getTextEditor(), message, FIX_IT);
            validator.errors.add(error);
        }
    }

    private void validate() {
        if (!pluginEnabledCheckbox.isSelected()) {
            return;
        }
        Validator validator = new Validator();
        validateField(validator, rtBinField, false, "Path to react-templates is invalid {{LINK}}");
        validateField(validator, nodeInterpreterField, false, "Path to node interpreter is invalid {{LINK}}");
//        if (!validateDirectory(customRulesPathField.getText(), true)) {
//            RTValidationInfo error = new RTValidationInfo(customRulesPathField, "Path to custom rules is invalid {{LINK}}", FIX_IT);
//            errors.add(error);
//        }
//        if (!validateDirectory(rulesPathField.getChildComponent().getText(), true)) {
//            RTValidationInfo error = new RTValidationInfo(rulesPathField.getChildComponent().getTextEditor(), "Path to rules is invalid {{LINK}}", FIX_IT);
//            errors.add(error);
//        }
        if (validator.isEmpty()) {
            getVersion();
        }
        packagesNotificationPanel.processErrors(validator);
    }

    private RTSettings settings;

    private void getVersion() {
        if (settings != null &&
                areEqual(nodeInterpreterField, settings.getNode()) &&
                areEqual(rtBinField, settings.getRtExe()) &&
                settings.getCwd().equals(project.getBasePath())
                ) {
            return;
        }
        System.out.println(project.getBasePath());
        String node = nodeInterpreterField.getChildComponent().getText();
        String rtBin = rtBinField.getChildComponent().getText();

        System.out.println("node: " + node + " rtBin: " + rtBin);

        settings = RTSettings.Companion.buildVersion(project.getBasePath(), node, rtBin);
//        settings.node = nodeInterpreterField.getChildComponent().getText();
//        settings.rtExecutablePath = rtBinField.getChildComponent().getText();
//        settings.cwd = project.getBasePath();
        try {
            String version = RTRunner.INSTANCE.runVersion(settings);
            versionLabel.setText(version.trim());

            List<String> versions = RTRunner.INSTANCE.listTargetVersions(settings);
            String[] array = new String[versions.size()];
            comboBoxTargetVersion.setModel(new DefaultComboBoxModel<String>(versions.toArray(array)));
//            for (String s : versions) {
//                comboBoxTargetVersion.addItem(s);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TextFieldWithHistory configWithDefaults(TextFieldWithHistoryWithBrowseButton field) {
        TextFieldWithHistory textFieldWithHistory = field.getChildComponent();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setMinimumAndPreferredWidth(0);
        return textFieldWithHistory;
    }

    private void configRTBinField() {
        configWithDefaults(rtBinField);
        SwingHelper.addHistoryOnExpansion(rtBinField.getChildComponent(), new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = RTFinder.searchForRTBin(getProjectPath());
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, rtBinField, "Select React-Templates Cli", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    private void configNodeField() {
        TextFieldWithHistory textFieldWithHistory = configWithDefaults(nodeInterpreterField);
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, new NotNullProducer<List<String>>() {
            @NotNull
            public List<String> produce() {
                List<File> newFiles = NodeDetectionUtil.listAllPossibleNodeInterpreters();
                return FileUtils.toAbsolutePath(newFiles);
            }
        });
        SwingHelper.installFileCompletionAndBrowseDialog(project, nodeInterpreterField, "Select Node Interpreter", FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "React-Templates";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        loadSettings();
        return panel;
    }

    private static boolean areEqual(TextFieldWithHistoryWithBrowseButton field, String value) {
        return field.getChildComponent().getText().equals(value);
    }

    private static boolean areEqual(JComboBox<?> field, String value) {
        Object item = field.getSelectedItem();
        return item == null && value == null || item != null && item.equals(value);
    }

    private static boolean areEqual(JCheckBox field, boolean value) {
        return field.isSelected() == value;
    }

    @Override
    public boolean isModified() {
        Settings s = getSettings();
        return !areEqual(pluginEnabledCheckbox, s.pluginEnabled) ||
                !areEqual(rtBinField, s.rtExecutable) ||
                !getModules().equals(s.modules) ||
                !areEqual(groupController, s.groupController) ||
                !areEqual(groupOtherFilesCheckBox, s.groupOther) ||
                !areEqual(watchAndCompileRtCheckBox, s.watchAndCompileRT) ||
                !areEqual(reactNativeCheckBox, s.reactNative) ||
                !areEqual(comboBoxTargetVersion, s.targetVersion) ||
                !areEqual(nodeInterpreterField, s.nodeInterpreter);
    }

    @Override
    public void apply() throws ConfigurationException {
        saveSettings();
        PsiManager.getInstance(project).dropResolveCaches();
        ProjectView.getInstance(project).refresh();
    }

    protected void saveSettings() {
        Settings settings = getSettings();
        settings.pluginEnabled = pluginEnabledCheckbox.isSelected();
        settings.rtExecutable = rtBinField.getChildComponent().getText();
        settings.nodeInterpreter = nodeInterpreterField.getChildComponent().getText();
        settings.modules = getModules();
        settings.groupController = groupController.isSelected();
        settings.groupOther = groupOtherFilesCheckBox.isSelected();
        settings.reactNative = reactNativeCheckBox.isSelected();
        settings.groupOther = groupOtherFilesCheckBox.isSelected();
        settings.watchAndCompileRT = watchAndCompileRtCheckBox.isSelected();
        settings.targetVersion = (String) comboBoxTargetVersion.getSelectedItem();
        project.getComponent(RTProjectComponent.class).validateSettings();
        DaemonCodeAnalyzer.getInstance(project).restart();
    }

    protected void loadSettings() {
        Settings settings = getSettings();
        pluginEnabledCheckbox.setSelected(settings.pluginEnabled);
        rtBinField.getChildComponent().setText(settings.rtExecutable);
        nodeInterpreterField.getChildComponent().setText(settings.nodeInterpreter);

        if (settings.modules.equals(RTRunner.AMD)) {
            AMDRadioButton.setSelected(true);
        } else if (settings.modules.equals(RTRunner.COMMONJS)) {
            commonJSRadioButton.setSelected(true);
        } else if (settings.modules.equals(RTRunner.TYPESCRIPT)) {
            typescriptRadioButton.setSelected(true);
        } else if (settings.modules.equals(RTRunner.ES6)) {
            ES6RadioButton.setSelected(true);
        } else {
            noneGlobalsRadioButton.setSelected(true);
        }

        groupController.setSelected(settings.groupController);
        reactNativeCheckBox.setSelected(settings.reactNative);
        watchAndCompileRtCheckBox.setSelected(settings.watchAndCompileRT);
        groupOtherFilesCheckBox.setSelected(settings.groupOther);
        comboBoxTargetVersion.setSelectedItem(settings.targetVersion);
        setEnabledState(settings.pluginEnabled);
    }

    @Override
    public void reset() {
        loadSettings();
    }

    @Override
    public void disposeUIResources() {
    }

    protected Settings getSettings() {
        return Settings.getInstance(project);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        usageLink = SwingHelper.createWebHyperlink(HOW_TO_USE_RT, HOW_TO_USE_LINK);
    }
}
