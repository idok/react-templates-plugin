package com.wix.rt.inspection;

import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.SeverityRegistrar;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.wix.ActualFile;
import com.wix.ThreadLocalActualFile;
import com.wix.annotator.ExternalLintAnnotationInput;
import com.wix.annotator.ExternalLintAnnotationResult;
import com.wix.annotator.InspectionUtil;
import com.wix.rt.RTBundle;
import com.wix.rt.RTProjectComponent;
import com.wix.rt.build.RTFileUtil;
import com.wix.rt.build.Result;
import com.wix.rt.build.VerifyMessage;
import com.wix.rt.cli.RTRunner;
import com.wix.rt.cli.RTSettings;
import com.wix.utils.Delayer;
import com.wix.utils.FileUtils;
import com.wix.utils.PsiUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author idok
 */
public class RTExternalAnnotator extends ExternalAnnotator<ExternalLintAnnotationInput, ExternalLintAnnotationResult<Result>> {

    public static final RTExternalAnnotator INSTANCE = new RTExternalAnnotator();
    private static final Logger LOG = Logger.getInstance(RTBundle.LOG_ID);
    private static final String MESSAGE_PREFIX = "RT: ";
    private static final Key<ThreadLocalActualFile> RT_TEMP_FILE_KEY = Key.create("RT_TEMP_FILE");

//    private static final ActualFileManager actualFileManager = new ActualFileManager(RT_TEMP_FILE_KEY, false, "rt", "-rt-tmp");

//    private static final int TABS = 4;
//    private int tabSize;

//    private static int getTabSize(@NotNull Editor editor) {
//        // Get tab size
//        int tabSize = 0;
//        Project project = editor.getProject();
//        PsiFile psifile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
//        CommonCodeStyleSettings commonCodeStyleSettings = new CommonCodeStyleSettings(psifile.getLanguage());
//        CommonCodeStyleSettings.IndentOptions indentOptions = commonCodeStyleSettings.getIndentOptions();
//
//        if (indentOptions != null) {
//            tabSize = commonCodeStyleSettings.getIndentOptions().TAB_SIZE;
//        }
//        if (tabSize == 0) {
//            tabSize = editor.getSettings().getTabSize(editor.getProject());
//        }
//        return tabSize;
//    }

    @Nullable
    @Override
    public ExternalLintAnnotationInput collectInformation(@NotNull PsiFile file) {
        return collectInformation(file, null);
    }

    @Nullable
    @Override
    public ExternalLintAnnotationInput collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return collectInformation(file, editor);
    }

    @Override
    public void apply(@NotNull PsiFile file, ExternalLintAnnotationResult<Result> annotationResult, @NotNull AnnotationHolder holder) {
        if (annotationResult == null) {
            return;
        }
        InspectionProjectProfileManager inspectionProjectProfileManager = InspectionProjectProfileManager.getInstance(file.getProject());
        SeverityRegistrar severityRegistrar = inspectionProjectProfileManager.getSeverityRegistrar();
        HighlightDisplayKey inspectionKey = RTInspection.getHighlightDisplayKey();
        HighlightSeverity severity = InspectionUtil.getSeverity(inspectionProjectProfileManager, inspectionKey, file);
//        RTProjectComponent component = annotationResult.input.project.getComponent(RTProjectComponent.class);
//        HighlightSeverity severity = getHighlightSeverity(warn, component.treatAsWarnings);
        EditorColorsScheme colorsScheme = annotationResult.input.colorsScheme;

        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document == null) {
            return;
        }
        if (annotationResult.result == null || annotationResult.result.warns == null) {
            LOG.warn("annotationResult.result == null");
            return;
        }
        for (VerifyMessage warn : annotationResult.result.warns) {
            TextAttributes forcedTextAttributes = InspectionUtil.getTextAttributes(colorsScheme, severityRegistrar, severity);
            /*Annotation annotation = */
            severity = getHighlightSeverity(warn, false);
            createAnnotation(holder, file, document, warn, severity, forcedTextAttributes, true);
//            if (annotation != null) {
//                int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
//                PsiElement lit = PsiUtil.getElementAtOffset(file, offset);
//                BaseActionFix actionFix = Fixes.getFixForRule(warn.rule, lit);
//                if (actionFix != null) {
//                    annotation.registerFix(actionFix, null, inspectionKey);
//                }
//                annotation.registerFix(new SuppressActionFix(warn.rule, lit), null, inspectionKey);
//            }
        }
    }

    private static HighlightSeverity getHighlightSeverity(VerifyMessage warn, boolean treatAsWarnings) {
        if (treatAsWarnings) {
            return HighlightSeverity.WARNING;
        }
        return warn.level.equals("ERROR") ? HighlightSeverity.ERROR : HighlightSeverity.WARNING;
    }

    @Nullable
    private static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull PsiFile file, @NotNull Document document, @NotNull VerifyMessage warn,
                                               @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes,
                                               boolean showErrorOnWholeLine) {
        if (warn.startOffset > -1 && warn.endOffset > -1) {
            return createAnnotationByOffset(holder, warn, severity, forcedTextAttributes);
        }
        if (warn.index > -1) {
            return createAnnotationByIndex(holder, file, document, warn, severity, forcedTextAttributes, showErrorOnWholeLine);
        }

        int line = Math.max(warn.line - 1, 0);
        int column = Math.max(warn.column, 0);

        if (line < 0 || line >= document.getLineCount()) {
            return null;
        }
        int lineEndOffset = document.getLineEndOffset(line);
        int lineStartOffset = document.getLineStartOffset(line);

        int errorLineStartOffset = StringUtil.lineColToOffset(document.getCharsSequence(), line, column);
//        int errorLineStartOffset = PsiUtil.calcErrorStartOffsetInDocument(document, lineStartOffset, lineEndOffset, column, tab);

        if (errorLineStartOffset == -1) {
            return null;
        }
//        PsiElement element = file.findElementAt(errorLineStartOffset);
        TextRange range;
        if (showErrorOnWholeLine) {
            range = new TextRange(lineStartOffset, lineEndOffset);
        } else {
//            int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
            PsiElement lit = PsiUtil.getElementAtOffset(file, errorLineStartOffset);
            range = lit.getTextRange();
//            range = new TextRange(errorLineStartOffset, errorLineStartOffset + 1);
        }

        Annotation annotation = InspectionUtil.createAnnotation(holder, severity, forcedTextAttributes, range, MESSAGE_PREFIX + warn.msg.trim());
        if (annotation != null) {
            annotation.setAfterEndOfLine(errorLineStartOffset == lineEndOffset);
        }
        return annotation;
    }

    @Nullable
    private static Annotation createAnnotationByIndex(@NotNull AnnotationHolder holder, @NotNull PsiFile file, @NotNull Document document, @NotNull VerifyMessage warn,
                                                      @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes,
                                                      boolean showErrorOnWholeLine) {
        int line = StringUtil.offsetToLineNumber(document.getCharsSequence(), warn.index);
        line = Math.max(line, 0);
        int lineEndOffset = document.getLineEndOffset(line);
        int start = document.getLineStartOffset(line);
        TextRange range;
        if (showErrorOnWholeLine) {
            range = new TextRange(start, lineEndOffset);
        } else {
//            int offset = StringUtil.lineColToOffset(document.getText(), warn.line - 1, warn.column);
            PsiElement lit = PsiUtil.getElementAtOffset(file, line);
            range = lit.getTextRange();
//            range = new TextRange(errorLineStartOffset, errorLineStartOffset + 1);
        }

        Annotation annotation = InspectionUtil.createAnnotation(holder, severity, forcedTextAttributes, range, MESSAGE_PREFIX + warn.msg.trim());
        if (annotation != null) {
            annotation.setAfterEndOfLine(line == lineEndOffset);
        }
        return annotation;
    }

    @Nullable
    private static Annotation createAnnotationByOffset(@NotNull AnnotationHolder holder, @NotNull VerifyMessage warn,
                                                       @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes) {
        TextRange range = new TextRange(warn.startOffset, warn.endOffset);
        return InspectionUtil.createAnnotation(holder, severity, forcedTextAttributes, range, MESSAGE_PREFIX + warn.msg.trim());
    }

    @Nullable
    private static ExternalLintAnnotationInput collectInformation(@NotNull PsiFile psiFile, @Nullable Editor editor) {
        if (psiFile.getContext() != null || !RTFileUtil.isRTFile(psiFile)) {
            return null;
        }
        VirtualFile virtualFile = psiFile.getVirtualFile();
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) {
            return null;
        }
        if (psiFile.getViewProvider() instanceof MultiplePsiFilesPerDocumentFileViewProvider) {
            return null;
        }
        Project project = psiFile.getProject();
        RTProjectComponent component = project.getComponent(RTProjectComponent.class);
        if (component == null || !component.isValidAndEnabled()) {
            return null;
        }
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document == null) {
            return null;
        }
        String fileContent = document.getText();
        if (StringUtil.isEmptyOrSpaces(fileContent)) {
            return null;
        }
        EditorColorsScheme colorsScheme = editor == null ? null : editor.getColorsScheme();
//        tabSize = getTabSize(editor);
//        tabSize = 4;
        return new ExternalLintAnnotationInput(project, psiFile, fileContent, colorsScheme);
    }

    @Nullable
    @Override
    public ExternalLintAnnotationResult<Result> doAnnotate(ExternalLintAnnotationInput collectedInfo) {
        ActualFile actualCodeFile = null;
        try {
            PsiFile file = collectedInfo.psiFile;
            if (!RTFileUtil.isRTFile(file)) return null;
            RTProjectComponent component = file.getProject().getComponent(RTProjectComponent.class);
            if (component == null || !component.isValidAndEnabled()) {
                return null;
            }

            String relativeFile;
            actualCodeFile = ActualFile.getOrCreateActualFile(RT_TEMP_FILE_KEY, file.getVirtualFile(), collectedInfo.fileContent);
            if (actualCodeFile == null || actualCodeFile.getActualFile() == null) {
                return null;
            }
            relativeFile = FileUtils.makeRelative(new File(file.getProject().getBasePath()), actualCodeFile.getActualFile());
            RTSettings settings = RTSettings.build(component.settings, file.getProject().getBasePath(), relativeFile);
            Result result = RTRunner.compile(settings);

            if (StringUtils.isNotEmpty(result.errorOutput)) {
                component.showInfoNotification(result.errorOutput, NotificationType.WARNING);
                return null;
            }
            Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
            if (document == null) {
                component.showInfoNotification("Error running RT inspection: Could not get document for file " + file.getName(), NotificationType.WARNING);
                LOG.error("Could not get document for file " + file.getName());
                return null;
            }
            return new ExternalLintAnnotationResult<Result>(collectedInfo, result);
        } catch (Exception e) {
            LOG.error("Error running RT inspection: ", e);
            showNotification("Error running RT inspection: " + e.getMessage(), NotificationType.ERROR);
//            return new ExternalLintAnnotationResult<Result>(collectedInfo, result); file level annotation
        } finally {
            if (actualCodeFile != null) {
                actualCodeFile.deleteTemp();
            }
        }
        return null;
    }

    private final Delayer delayer = new Delayer(TimeUnit.SECONDS.toMillis(5L));

    public void showNotification(String content, NotificationType type) {
        if (delayer.should()) {
            RTProjectComponent.showNotification(content, type);
            delayer.done();
        }
    }
}