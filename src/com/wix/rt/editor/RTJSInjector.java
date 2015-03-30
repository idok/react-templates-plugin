package com.wix.rt.editor;

import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.lang.javascript.JSTargetedInjector;
import com.intellij.lang.javascript.JavascriptLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.xml.XmlAttributeValueImpl;
import com.intellij.psi.impl.source.xml.XmlTextImpl;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTokenType;
import com.wix.rt.actions.RTActionUtil;
import com.wix.rt.codeInsight.DirectiveUtil;
import com.wix.rt.codeInsight.RTAttributes;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RTJSInjector implements MultiHostInjector, JSTargetedInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        final Project project = context.getProject();
        if (!RTActionUtil.isRTEnabled(project)) return;

        final PsiElement parent = context.getParent();
        if (context instanceof XmlAttributeValueImpl && parent instanceof XmlAttribute) {
            final String value = context.getText();
            final int start = value.startsWith("'") || value.startsWith("\"") ? 1 : 0;
            final int end = value.endsWith("'") || value.endsWith("\"") ? 1 : 0;
            final int length = value.length();
            final String attributeName = DirectiveUtil.normalizeAttributeName(((XmlAttribute) parent).getName());
            if (attributeName.startsWith("on")) {
                inject(registrar, context, new TextRange(start, length - end));
                return;
            }
//            if (RTAttributes.isJSExpressionAttribute((XmlAttribute) parent) && length > 1) {
//                inject(registrar, context, new TextRange(0, length));
//                return;
//            }
            if (RTAttributes.isRTJSExpressionAttribute((XmlAttribute) parent) && length > 1) {
                inject(registrar, context, new TextRange(start, length - end));
                return;
            }
        }

        if (context instanceof XmlAttributeValueImpl) {
            System.out.println(((XmlAttributeValueImpl) context).getValue());
        }

        if (context instanceof XmlTextImpl || context instanceof XmlAttributeValueImpl) {
            final String start = RTJSBracesUtil.getInjectionStart(project);
            final String end = RTJSBracesUtil.getInjectionEnd(project);

            if (RTJSBracesUtil.hasConflicts(start, end, context)) return;

            final String text = context.getText();
            int startIndex;
            int endIndex = -1;
            if (text.length() < 2) {
                return;
            }
            do {startIndex = text.indexOf(start, endIndex);
                int afterStart = startIndex + start.length();
                endIndex = startIndex >= 0 ? text.indexOf(end, afterStart) : -1;
                endIndex = endIndex > 0 ? endIndex : text.length() - 1;
                final PsiElement injectionCandidate = startIndex >= 0 ? context.findElementAt(startIndex) : null;
                if (injectionCandidate != null && injectionCandidate.getNode().getElementType() != XmlTokenType.XML_COMMENT_CHARACTERS && !(injectionCandidate instanceof OuterLanguageElement)) {
                    if (afterStart < endIndex) {
                        inject(registrar, context, new TextRange(afterStart, endIndex));
                    }
//                    System.out.println(new TextRange(afterStart, endIndex));
                }
            } while (startIndex >= 0);
        }
    }

    private static void inject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context, TextRange textRange) {
        registrar.startInjecting(JavascriptLanguage.INSTANCE)
                .addPlace(null, null, (PsiLanguageInjectionHost) context, textRange)
                .doneInjecting();
    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Arrays.asList(XmlTextImpl.class, XmlAttributeValueImpl.class);
    }
}
