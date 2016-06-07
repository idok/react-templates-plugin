package com.wix.rt.codeInsight;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlAttributeDescriptorsProvider;
import com.intellij.xml.impl.schema.AnyXmlAttributeDescriptor;
import com.wix.rt.actions.RTActionUtil;
import com.wix.rt.build.RTFileUtil;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RTAttributeDescriptorsProvider implements XmlAttributeDescriptorsProvider {

    @Override
    public XmlAttributeDescriptor[] getAttributeDescriptors(XmlTag xmlTag) {
        if (xmlTag instanceof HtmlTag /*&& RTFileUtil.hasRTExt(xmlTag.getContainingFile())*/) {
            final Project project = xmlTag.getProject();
            if (RTActionUtil.isRTEnabled(project)) {
                final Map<String, XmlAttributeDescriptor> result = new LinkedHashMap<String, XmlAttributeDescriptor>();
                for (String attr : RTAttributes.ALL_ATTRIBUTES) {
                    result.put(attr, new RTXmlAttributeDescriptor(attr));
                }
//                result.put("rt-repeat", new RTXmlAttributeDescriptor("rt-repeat")); // x in [javascript]
//                result.put("rt-if", new RTXmlAttributeDescriptor("rt-if")); //[javascript]
//                result.put("rt-scope", new RTXmlAttributeDescriptor("rt-scope")); //a as b;c as d
//                result.put("rt-class", new RTXmlAttributeDescriptor("rt-class")); // [javascript]
//                result.put("rt-props", new RTXmlAttributeDescriptor("rt-props")); // [javascript]
                return result.values().toArray(new XmlAttributeDescriptor[result.size()]);

    //            return new XmlAttributeDescriptor[]{
    //                    new AnyXmlAttributeDescriptor("rt-if"),
    //                    new AnyXmlAttributeDescriptor("rt-repeat"),
    //                    new AnyXmlAttributeDescriptor("rt-scope"),
    //                    new AnyXmlAttributeDescriptor("rt-props"),
    //                    new AnyXmlAttributeDescriptor("rt-class"),
    //                    new AnyXmlAttributeDescriptor("rt-require")
    //            };
            }
        }
        return XmlAttributeDescriptor.EMPTY;
    }

    private static boolean tagMatches(String tagName, String tag) {
        if (StringUtil.isEmpty(tag) || StringUtil.equalsIgnoreCase(tag, "ANY")) {
            return true;
        }
        for (String s : tag.split(",")) {
            if (StringUtil.equalsIgnoreCase(tagName, s.trim())) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(final String attrName, XmlTag xmlTag) {
        if (xmlTag != null) {
            if (RTAttributes.isRTAttribute(attrName)) {
                return new AnyXmlAttributeDescriptor(attrName);
            }
        }
        return null;
    }
}
