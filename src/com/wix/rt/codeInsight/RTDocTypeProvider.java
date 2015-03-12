package com.wix.rt.codeInsight;

import com.intellij.psi.impl.source.xml.XmlDoctypeImpl;
import com.intellij.psi.xml.XmlDoctype;
import com.intellij.psi.xml.XmlFile;
import com.intellij.xml.util.HtmlDoctypeProvider;
import org.jetbrains.annotations.Nullable;

public class RTDocTypeProvider implements HtmlDoctypeProvider {
    @Nullable
    @Override
    public XmlDoctype getDoctype(XmlFile file) {
        return new RTDoctype();
    }

    public static class RTDoctype extends XmlDoctypeImpl {
    }
}
