package com.wix.rt.codeInsight;

import com.intellij.psi.xml.XmlAttribute;
import org.apache.commons.lang.ArrayUtils;

public final class RTAttributes {

//  result.put("rt-repeat", new RTXmlAttributeDescriptor("rt-repeat")); // x in [javascript]
//  result.put("rt-if", new RTXmlAttributeDescriptor("rt-if")); //[javascript]
//  result.put("rt-scope", new RTXmlAttributeDescriptor("rt-scope")); //a as b;c as d
//  result.put("rt-class", new RTXmlAttributeDescriptor("rt-class")); // [javascript]
//  result.put("rt-props", new RTXmlAttributeDescriptor("rt-props")); // [javascript]

    //https://facebook.github.io/react/docs/special-non-dom-attributes.html
    public static final String[] REACT_ATTRIBUTES = {"ref", "key", "dangerouslySetInnerHTML", "valueLink", "className"};
    public static final String[] ATTRIBUTES = {"rt-repeat", "rt-if", "rt-scope", "rt-class", "rt-props"};
    public static final String[] ALL_ATTRIBUTES = (String[]) ArrayUtils.addAll(ATTRIBUTES, REACT_ATTRIBUTES);

    private RTAttributes() {
    }

    public static boolean isRTAttribute(String attrName) {
        return isAttribute(attrName, ALL_ATTRIBUTES);
    }

    public static boolean isJSAttribute(String attrName) {
        return isAttribute(attrName, ATTRIBUTES);
    }

    public static boolean isAttribute(String attrName, String[] attributes) {
        for (String attr : attributes) {
            if (attrName.equals(attr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJSExpressionAttribute(XmlAttribute parent) {
        final String attributeName = DirectiveUtil.normalizeAttributeName(parent.getName());
        return attributeName.equals("rt-if");
    }

    public static boolean isRTJSExpressionAttribute(XmlAttribute parent) {
        final String attributeName = DirectiveUtil.normalizeAttributeName(parent.getName());
        return isJSAttribute(attributeName);
//        final JSOffsetBasedImplicitElement directive = AngularIndexUtil.resolve(parent.getProject(), AngularDirectivesDocIndex.INDEX_ID, attributeName);
//        if (directive != null) {
//            final String restrict = directive.getTypeString();
//            final String param = restrict.split(";", -1)[2];
//            return param.endsWith("expression") || param.startsWith("string");
//        }
//        return false;
    }
}
