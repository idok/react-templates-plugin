package com.wix.rt.codeInsight;

import com.intellij.openapi.util.text.StringUtil;

import java.util.regex.Pattern;

/**
* @author Dennis.Ushakov
*/
public final class DirectiveUtil {
    private static final Pattern COMPILE = Pattern.compile("(?=[A-Z])");

    private DirectiveUtil() {
    }

    public static String getAttributeName(final String text) {
        final String[] split = COMPILE.split(StringUtil.unquoteString(text));
        for (int i = 0; i < split.length; i++) {
            split[i] = StringUtil.decapitalize(split[i]);
        }
        return StringUtil.join(split, "-");
    }

    public static String normalizeAttributeName(String name) {
        if (name == null) return null;
        if (name.startsWith("data-")) {
            name = name.substring(5);
        } else if (name.startsWith("x-")) {
            name = name.substring(2);
        }
        name = name.replace(':', '-');
        name = name.replace('_', '-');
        if (name.endsWith("-start")) {
            name = name.substring(0, name.length() - 6);
        } else if (name.endsWith("-end")) {
            name = name.substring(0, name.length() - 4);
        }
        return name;
    }
}
