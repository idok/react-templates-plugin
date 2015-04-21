package com.wix.rt.lang.lexer;

import com.intellij.psi.tree.IElementType;
import com.wix.rt.lang.RTLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dennis.Ushakov
 */
public class RTTokenType extends IElementType {
    public RTTokenType(@NotNull @NonNls String debugName) {
        super(debugName, RTLanguage.INSTANCE);
    }
}
