package com.wix.rt.lang.parser;

import com.intellij.psi.tree.IElementType;
import com.wix.rt.lang.RTLanguage;

/**
 * @author Dennis.Ushakov
 */
public interface RTElementTypes {
    IElementType REPEAT_EXPRESSION = new IElementType("REPEAT_EXPRESSION", RTLanguage.INSTANCE);
    IElementType FILTER_EXPRESSION = new IElementType("FILTER_EXPRESSION", RTLanguage.INSTANCE);
    IElementType AS_EXPRESSION = new IElementType("AS_EXPRESSION", RTLanguage.INSTANCE);
}
