package com.wix.rt.codeInsight;

import com.google.common.base.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.xml.SchemaPrefix;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.HtmlXmlExtension;
import com.wix.rt.actions.RTActionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RTHtmlExtension extends HtmlXmlExtension {
    @Override
    public boolean isAvailable(PsiFile file) {
        return super.isAvailable(file) && RTActionUtil.isRTEnabled(file.getProject());
    }

    @Override
    public boolean isRequiredAttributeImplicitlyPresent(XmlTag tag, String attrName) {
//        for (XmlAttribute attribute : tag.getAttributes()) {
//            if (("rt-" + attrName).equals(DirectiveUtil.normalizeAttributeName(attribute.getName()))) {
//                return true;
//            }
//        }
        return super.isRequiredAttributeImplicitlyPresent(tag, attrName);
    }

    @Override
    public SchemaPrefix getPrefixDeclaration(XmlTag context, String namespacePrefix) {
//        if ("rt".equals(namespacePrefix)) {
//            return new SchemaPrefix(null, null, namespacePrefix);
//        }
        return super.getPrefixDeclaration(context, namespacePrefix);
    }

    @NotNull
    @Override
    public List<TagInfo> getAvailableTagNames(@NotNull XmlFile file, @NotNull XmlTag context) {
        List<TagInfo> list = super.getAvailableTagNames(file, context);
        list.add(new TagInfo(RTTagDescriptorsProvider.RT_REQUIRE, "http://www.w3.org/1999/html"));
        List<String> importedTags = loadImportedTags(file, context);
        for (String importedTag : importedTags) {
            list.add(new TagInfo(importedTag, "http://www.w3.org/1999/html") {
                @Nullable
                @Override
                public PsiElement getDeclaration() {
                    return super.getDeclaration();
                }
            });
        }
        return list;
    }

    public static List<String> loadImportedTags(@NotNull XmlFile file, @NotNull XmlTag context) {
//        PsiElement[] arr = file.getRootTag().getChildren();
//        Collection<HtmlTag> tags = PsiTreeUtil.findChildrenOfType(file, HtmlTag.class);
        PsiElement[] reqTags = PsiTreeUtil.collectElements(file, new PsiElementFilter() {
            @Override
            public boolean isAccepted(PsiElement element) {
                return element instanceof HtmlTag && ((HtmlTag) element).getName().equals(RTTagDescriptorsProvider.RT_REQUIRE);
            }
        });

        List<String> importedTags = new ArrayList<String>();
        for (PsiElement elem : reqTags) {
            String as = ((HtmlTag) elem).getAttributeValue("as");
            if (!Strings.isNullOrEmpty(as)) {
                importedTags.add(as);
            }
        }
        return importedTags;
    }
}
