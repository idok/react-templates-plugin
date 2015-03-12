package com.wix.rt;

import com.intellij.lang.documentation.DocumentationProviderEx;
import com.intellij.lang.javascript.index.JSNamedElementProxy;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.xml.HtmlXmlExtension;
import com.intellij.xml.util.documentation.XHtmlDocumentationProvider;
import com.wix.rt.codeInsight.DirectiveUtil;
//import com.wix.rt.index.AngularDirectivesDocIndex;
//import com.wix.rt.index.AngularIndexUtil;
import org.intellij.lang.regexp.psi.RegExpElement;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RTDocumentationProvider extends DocumentationProviderEx {
    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor,
                                                    @NotNull PsiFile file,
                                                    @Nullable PsiElement element) {
        final IElementType elementType = element != null ? element.getNode().getElementType() : null;
        if (elementType == XmlTokenType.XML_NAME || elementType == XmlTokenType.XML_TAG_NAME) {
            return PsiTreeUtil.getParentOfType(element, XmlAttribute.class, false);
        }
        if (elementType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN) {
            return PsiTreeUtil.getParentOfType(element, XmlAttribute.class, false);
        }
        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof XmlAttribute) {
            if (((XmlAttribute) element).getName().startsWith("rt-")) {
                return Collections.singletonList("http://www.github.com/wix/react-templates");
            }
        }
        return null;
    }

    @Nullable
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof XmlAttribute) {
            XmlAttribute xmlAttr = (XmlAttribute) element;
            if (xmlAttr.getName().startsWith("rt-")) {
                return "<strong>" + xmlAttr.getName() + "</strong> documentation is not available yet";
            }
        }
        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof XmlAttribute) {
            XmlAttribute xmlAttr = (XmlAttribute) element;
            String name = xmlAttr.getName();
            if (name.startsWith("rt-")) {
                return getDoc(name);
            }
        }
        return "documentation is not available yet";
    }

    private static String getDoc(String tag) {
        // return RTBundle.message("doc." + name);
        String s;
        try {
            s = FileUtil.loadTextAndClose(RTDocumentationProvider.class.getResourceAsStream("/tagDescriptions/" + tag + ".html"));
        } catch (IOException e) {
//      throw new IncorrectOperationException(RTBundle.message("error.cannot.read", formName), (Throwable)e);
            return "null";
        }
        return StringUtil.convertLineSeparators(s);
    }
}
