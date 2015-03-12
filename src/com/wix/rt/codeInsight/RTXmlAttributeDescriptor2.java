package com.wix.rt.codeInsight;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.ArrayUtil;
import com.intellij.xml.impl.BasicXmlAttributeDescriptor;
import com.intellij.xml.impl.XmlAttributeDescriptorEx;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by idok on 11/17/14.
 * RT xml attribute descriptor with name
 */
public class RTXmlAttributeDescriptor2 extends BasicXmlAttributeDescriptor implements XmlAttributeDescriptorEx {

    private final String name;

    public RTXmlAttributeDescriptor2(String name) {
        this.name = name;
    }

    @Nullable
    @Override
    public String handleTargetRename(@NotNull @NonNls String newTargetName) {
        return DirectiveUtil.getAttributeName(newTargetName);
    }

    @Override
    public boolean isRequired() {
        return true;
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
        return ArrayUtil.EMPTY_OBJECT_ARRAY;
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

    @Override
    public String validateValue(XmlElement context, String value) {
        return super.validateValue(context, value);
    }
}
