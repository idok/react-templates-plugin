package com.wix.rt.lang.lexer;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

import java.io.Reader;

/**
 * @author Dennis.Ushakov
 */
public class RTLexer extends MergingLexerAdapter {
    public RTLexer() {
        super(new FlexAdapter(new _RTLexer((Reader) null)), TokenSet.create(JSTokenTypes.STRING_LITERAL));
    }
}
