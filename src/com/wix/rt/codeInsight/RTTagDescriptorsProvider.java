package com.wix.rt.codeInsight;

import com.intellij.codeInsight.completion.XmlTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.javascript.index.JSNamedElementProxy;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.xml.XmlElementDescriptorProvider;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlTagNameProvider;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import com.wix.rt.RTProjectComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RTTagDescriptorsProvider implements XmlElementDescriptorProvider, XmlTagNameProvider {

    public static final String RT_REQUIRE = "rt-require";
    public static final String AS = "as";
    public static final String DEPENDENCY = "dependency";

    @Override
    public void addTagNameVariants(final List<LookupElement> elements, @NotNull XmlTag xmlTag, String prefix) {
//        System.out.println("");
//        if (!(xmlTag instanceof HtmlTag && AngularIndexUtil.hasAngularJS(xmlTag.getProject()))) return;
//
//        final Project project = xmlTag.getProject();
//        DirectiveUtil.processTagDirectives(project, new Processor<JSNamedElementProxy>() {
//            @Override
//            public boolean process(JSNamedElementProxy directive) {
//                addLookupItem(elements, directive);
//                return true;
//            }
//        });
    }

    private static void addLookupItem(List<LookupElement> elements, JSNamedElementProxy directive) {
        elements.add(LookupElementBuilder.create(directive).withInsertHandler(XmlTagInsertHandler.INSTANCE));
    }

    @Nullable
    @Override
    public XmlElementDescriptor getDescriptor(XmlTag xmlTag) {
//        System.out.println("getDescriptor " + xmlTag);

        if (!(xmlTag instanceof HtmlTag && RTProjectComponent.isEnabled(xmlTag.getProject()))) {
            return null;
        }
        final String directiveName = DirectiveUtil.normalizeAttributeName(xmlTag.getName());
        if (directiveName.equals(RT_REQUIRE)) {
            return new RTRequireTagDescriptor(RT_REQUIRE, xmlTag);
        }

        List<String> tags = RTHtmlExtension.loadImportedTags((XmlFile) xmlTag.getContainingFile(), xmlTag);
        for (String tag : tags) {
            if (tag.equals(directiveName)) {
                return new RTClassTagDescriptor(directiveName, xmlTag);
            }
        }
        // TODO: support required tags
        //return new AnyXmlElementDescriptor()
        return null;
    }
}
