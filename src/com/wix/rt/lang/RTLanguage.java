package com.wix.rt.lang;

import com.intellij.lang.javascript.DialectOptionHolder;
import com.intellij.lang.javascript.JSLanguageDialect;

public class RTLanguage extends JSLanguageDialect {
    public static final RTLanguage INSTANCE = new RTLanguage();

    protected RTLanguage() {
        super("RT", DialectOptionHolder.OTHER);
    }

    @Override
    public String getFileExtension() {
        return "js";
    }
}
