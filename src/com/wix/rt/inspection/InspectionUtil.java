package com.wix.rt.inspection;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.HighlightDisplayKey;
import com.intellij.codeInsight.daemon.impl.HighlightInfoType;
import com.intellij.codeInsight.daemon.impl.SeverityRegistrar;
import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InspectionUtil {
    private InspectionUtil() {
    }

    @NotNull
    private static EditorColorsScheme getColorsScheme(@Nullable EditorColorsScheme customScheme) {
        return customScheme == null ? EditorColorsManager.getInstance().getGlobalScheme() : customScheme;
    }

    @NotNull
    public static TextAttributes getTextAttributes(@Nullable EditorColorsScheme editorColorsScheme, @NotNull SeverityRegistrar severityRegistrar, @NotNull HighlightSeverity severity) {
        TextAttributes textAttributes = severityRegistrar.getTextAttributesBySeverity(severity);
        if (textAttributes == null) {
            EditorColorsScheme colorsScheme = getColorsScheme(editorColorsScheme);
            HighlightInfoType.HighlightInfoTypeImpl infoType = severityRegistrar.getHighlightInfoTypeBySeverity(severity);
            TextAttributesKey key = infoType.getAttributesKey();
            return colorsScheme.getAttributes(key);
        } else {
            return textAttributes;
        }
    }

    @Nullable
    public static Annotation createAnnotation(@NotNull AnnotationHolder holder, @NotNull HighlightSeverity severity, @Nullable TextAttributes forcedTextAttributes, @NotNull TextRange range, @NotNull String message) {
        Annotation annotation;
        if (forcedTextAttributes == null) {
            if (severity.equals(HighlightSeverity.ERROR)) {
                annotation = holder.createErrorAnnotation(range, message);
            } else {
                annotation = holder.createWarningAnnotation(range, message);
            }
        } else {
            annotation = holder.createAnnotation(severity, range, message);
            annotation.setEnforcedTextAttributes(forcedTextAttributes);
        }
        annotation.setNeedsUpdateOnTyping(false);
        return annotation;
    }

    public static String getShortName(Class<?> aClass) {
        return InspectionProfileEntry.getShortName(aClass.getSimpleName());
    }

    @NotNull
    public static HighlightSeverity getSeverity(@NotNull InspectionProjectProfileManager inspectionProjectProfileManager, @NotNull HighlightDisplayKey inspectionKey, @NotNull PsiFile file) {
        InspectionProfile inspectionProfile = inspectionProjectProfileManager.getInspectionProfile();
        HighlightDisplayLevel errorLevel = inspectionProfile.getErrorLevel(inspectionKey, file);
        if (errorLevel == null) {
            errorLevel = HighlightDisplayLevel.WARNING;
        }
        return errorLevel.getSeverity();
    }
}
