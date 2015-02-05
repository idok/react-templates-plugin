package com.wix.rt.codeInsight;

import com.intellij.psi.xml.XmlAttribute;

public final class RTAttributes {

//                result.put("rt-repeat", new RTXmlAttributeDescriptor("rt-repeat")); // x in [javascript]
//                result.put("rt-if", new RTXmlAttributeDescriptor("rt-if")); //[javascript]
//                result.put("rt-scope", new RTXmlAttributeDescriptor("rt-scope")); //a as b;c as d
//                result.put("rt-class", new RTXmlAttributeDescriptor("rt-class")); // [javascript]
//                result.put("rt-props", new RTXmlAttributeDescriptor("rt-props")); // [javascript]

    //https://facebook.github.io/react/docs/special-non-dom-attributes.html
    public static final String[] REACT_ATTRIBUTES = {"rt-repeat", "rt-if", "rt-scope", "rt-class", "rt-props", "ref", "key", "dangerouslySetInnerHTML", "valueLink"};
    public static final String[] ATTRIBUTES = {"rt-repeat", "rt-if", "rt-scope", "rt-class", "rt-props", "ref", "key", "dangerouslySetInnerHTML", "valueLink"};

    private RTAttributes() {
    }

    public static boolean isRTAttribute(String attrName) {
        for (String attr : RTAttributes.ATTRIBUTES) {
            if (attrName.equals(attr)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJSExpressionAttribute(XmlAttribute parent) {
        final String attributeName = DirectiveUtil.normalizeAttributeName(parent.getName());
        return isRTAttribute(attributeName);
//        final JSOffsetBasedImplicitElement directive = AngularIndexUtil.resolve(parent.getProject(), AngularDirectivesDocIndex.INDEX_ID, attributeName);
//        if (directive != null) {
//            final String restrict = directive.getTypeString();
//            final String param = restrict.split(";", -1)[2];
//            return param.endsWith("expression") || param.startsWith("string");
//        }
//        return false;
    }
}
