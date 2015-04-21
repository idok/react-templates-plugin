package com.wix.rt.lang.lexer;

import com.intellij.lang.javascript.JSTokenTypes;

/**
 * @author Dennis.Ushakov
 */
public interface RTTokenTypes extends JSTokenTypes {
  RTTokenType ESCAPE_SEQUENCE = new RTTokenType("ESCAPE_SEQUENCE");
  RTTokenType INVALID_ESCAPE_SEQUENCE = new RTTokenType("INVALID_ESCAPE_SEQUENCE");
//  RTTokenType TRACK_BY_KEYWORD = new RTTokenType("TRACK_BY_KEYWORD");
  RTTokenType ONE_TIME_BINDING = new RTTokenType("ONE_TIME_BINDING");
}
