package com.wix.rt.inspection;

import com.google.common.base.Joiner;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInspection.*;
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool;
import com.intellij.ide.actions.ShowSettingsUtilImpl;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.options.ex.SingleConfigurableEditor;
//import com.intellij.openapi.options.newEditor.OptionsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.ui.HyperlinkAdapter;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.util.containers.ContainerUtil;
import com.wix.annotator.InspectionUtil;
import com.wix.rt.RTBundle;
import com.wix.rt.settings.RTSettingsPage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.util.List;

public class RTInspection extends LocalInspectionTool implements UnfairLocalInspectionTool {

//    public static final String INSPECTION_SHORT_NAME = "RTInspection";
//    public static final Key<RTInspection> KEY = Key.create(INSPECTION_SHORT_NAME);

    private static final Logger LOG = Logger.getInstance(RTBundle.LOG_ID);

    @NotNull
    public String getDisplayName() {
        return RTBundle.message("rt.property.inspection.display.name");
    }

    @NotNull
    public String getShortName() {
        return InspectionUtil.getShortName(RTInspection.class);
    }

    @NotNull
    public static HighlightDisplayKey getHighlightDisplayKeyByClass(@NotNull Class<?> inspectionClass) {
        String id = InspectionUtil.getShortName(inspectionClass);
        HighlightDisplayKey key = HighlightDisplayKey.find(id);
        if (key == null) {
            key = new HighlightDisplayKey(id, id);
        }
        return key;
    }

    public static HighlightDisplayKey getHighlightDisplayKey() {
        return getHighlightDisplayKeyByClass(RTInspection.class);
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        return ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(file, manager, isOnTheFly, new RTExternalAnnotator());
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new ExternalAnnotatorInspectionVisitor(holder, new RTExternalAnnotator(), isOnTheFly);
    }

    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        HyperlinkLabel settingsLink = createHyperLink();
        panel.setBorder(IdeBorderFactory.createTitledBorder(getDisplayName() + " options"));
        panel.add(settingsLink);
        return panel;
    }

    @NotNull
    public String getId() {
        return "Settings.JavaScript.Linters.RT";
    }

    @NotNull
    private HyperlinkLabel createHyperLink() {
        //JSBundle.message("settings.javascript.root.configurable.name")
        List<String> path = ContainerUtil.newArrayList("HTML", getDisplayName());

        String title = Joiner.on(" / ").join(path);
        final HyperlinkLabel settingsLink = new HyperlinkLabel(title);
        settingsLink.addHyperlinkListener(new HyperlinkAdapter() {
            public void hyperlinkActivated(HyperlinkEvent e) {
//                DataContext dataContext = DataManager.getInstance().getDataContext(settingsLink);
//                OptionsEditor optionsEditor = OptionsEditor.KEY.getData(dataContext);
//                if (optionsEditor == null) {
//                    Project project = CommonDataKeys.PROJECT.getData(dataContext);
//                    if (project != null) {
//                        showSettings(project);
//                    }
//                    return;
//                }
//                Configurable configurable = optionsEditor.findConfigurableById(RTInspection.this.getId());
//                if (configurable != null) {
//                    optionsEditor.clearSearchAndSelect(configurable);
//                }
            }
        });
        return settingsLink;
    }

    public static void showSettings(Project project) {
        RTSettingsPage configurable = new RTSettingsPage(project);
        String dimensionKey = ShowSettingsUtilImpl.createDimensionKey(configurable);
        SingleConfigurableEditor singleConfigurableEditor = new SingleConfigurableEditor(project, configurable, dimensionKey, false);
        singleConfigurableEditor.show();
    }

//    @Override
//    public boolean isSuppressedFor(@NotNull PsiElement element) {
//        return false;
//    }
//
//    @NotNull
//    @Override
//    public SuppressQuickFix[] getBatchSuppressActions(@Nullable PsiElement element) {
//        return new SuppressQuickFix[0];
//    }
}