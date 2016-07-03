package com.wix.rt.codeInsight;

import com.intellij.psi.PsiElement;
import com.intellij.xml.impl.BasicXmlAttributeDescriptor;
import com.intellij.xml.impl.XmlAttributeDescriptorEx;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * RT Xml Attribute Descriptor
 * Created by idok on 11/17/14.
 */
public class RTXmlAttributeDescriptor extends BasicXmlAttributeDescriptor implements XmlAttributeDescriptorEx {

    private final String name;

    public RTXmlAttributeDescriptor(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String handleTargetRename(@NotNull @NonNls String newTargetName) {
        return DirectiveUtil.getAttributeName(newTargetName);
    }

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public boolean hasIdType() {
        return false;
    }

    @Override
    public boolean hasIdRefType() {
        return false;
    }

    @Override
    public boolean isEnumerated() {
        return false;
    }

    @Override
    public PsiElement getDeclaration() {
        return null;
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
        return new Object[0];
    }

    @Override
    public boolean isFixed() {
        return false;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public String[] getEnumeratedValues() {
        return new String[0];
    }
}
