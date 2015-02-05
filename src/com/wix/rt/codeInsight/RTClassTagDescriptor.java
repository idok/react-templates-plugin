package com.wix.rt.codeInsight;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlElementsGroup;
import com.intellij.xml.XmlNSDescriptor;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * rt-require tag descriptor
 */
public class RTClassTagDescriptor implements XmlElementDescriptor {

    private final String name;
    private final PsiElement psiElement;

    public RTClassTagDescriptor(String name, PsiElement psiElement) {
        this.name = name;
        this.psiElement = psiElement;
    }

    @Override
    public String getQualifiedName() {
        return name;
    }

    @Override
    public String getDefaultName() {
        return name;
    }

    @Override
    public XmlElementDescriptor[] getElementsDescriptors(XmlTag context) {
        XmlDocumentImpl xmlDocument = PsiTreeUtil.getParentOfType(context, XmlDocumentImpl.class);
        if (xmlDocument == null) return EMPTY_ARRAY;
        return xmlDocument.getRootTagNSDescriptor().getRootElementsDescriptors(xmlDocument);
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag childTag, XmlTag contextTag) {
        XmlTag parent = contextTag.getParentTag();
        if (parent == null) return null;
        final XmlNSDescriptor descriptor = parent.getNSDescriptor(childTag.getNamespace(), true);
        return descriptor == null ? null : descriptor.getElementDescriptor(childTag);
    }

//    @Override
//    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
//        final String string = getDeclaration().getIndexItem().getTypeString();
//        final String attributes = string.split(";", -1)[3];
//        final String[] split = attributes.split(",");
//        final XmlAttributeDescriptor[] result;
//        if (split.length == 1 && split[0].isEmpty()) {
//            result = XmlAttributeDescriptor.EMPTY;
//        } else {
//            result = new XmlAttributeDescriptor[split.length];
//            for (int i = 0; i < split.length; i++) {
//                result[i] = new AnyXmlAttributeDescriptor(DirectiveUtil.getAttributeName(split[i]));
//            }
//        }
//
//        final XmlAttributeDescriptor[] commonAttributes = RelaxedHtmlFromSchemaElementDescriptor.getCommonAttributeDescriptors(context);
//        return RelaxedHtmlFromSchemaElementDescriptor.addAttrDescriptorsForFacelets(context, ArrayUtil.mergeArrays(result, commonAttributes));
//    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(@Nullable XmlTag context) {
        // final Project project = xmlTag.getProject();
        final Map<String, XmlAttributeDescriptor> result = new LinkedHashMap<String, XmlAttributeDescriptor>();
//        result.put(RTTagDescriptorsProvider.DEPENDENCY, new RTXmlAttributeDescriptor2(RTTagDescriptorsProvider.DEPENDENCY));
//        result.put(RTTagDescriptorsProvider.AS, new RTXmlAttributeDescriptor2(RTTagDescriptorsProvider.AS));
        // <rt-require dependency="./CodeMirrorEditor" as="CodeEditor"/>
        return result.values().toArray(new XmlAttributeDescriptor[result.size()]);
        // return new XmlAttributeDescriptor[0];
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(XmlAttribute attribute) {
        return getAttributeDescriptor(attribute.getName(), attribute.getParent());
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(@NonNls final String attributeName, @Nullable XmlTag context) {
        return ContainerUtil.find(getAttributesDescriptors(context), new Condition<XmlAttributeDescriptor>() {
            @Override
            public boolean value(XmlAttributeDescriptor descriptor) {
                return attributeName.equals(descriptor.getName());
            }
        });
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return null;
    }

    @Nullable
    @Override
    public XmlElementsGroup getTopGroup() {
        return null;
    }

    @Override
    public int getContentType() {
        return CONTENT_TYPE_ANY;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return null;
    }

//    @Override
//    public JSNamedElementProxy getDeclaration() {
//        return myDeclaration;
//    }

    @Override
    public PsiElement getDeclaration() {
        return psiElement;
    }

    @Override
    public String getName(PsiElement context) {
        return getName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void init(PsiElement element) {
    }

    @Override
    public Object[] getDependences() {
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
    }
}
