package com.wix.rt.lang.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.javascript.JavascriptParserDefinition;
import com.intellij.lang.javascript.types.JSFileElementType;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.wix.rt.lang.RTLanguage;
import com.wix.rt.lang.lexer.RTLexer;
import com.wix.rt.lang.psi.RTAsExpression;
import com.wix.rt.lang.psi.RTFilterExpression;
import com.wix.rt.lang.psi.RTRepeatExpression;
import org.jetbrains.annotations.NotNull;

/**
 * @author Dennis.Ushakov
 */
public class RTParserDefinition extends JavascriptParserDefinition {
    private static final IFileElementType FILE = JSFileElementType.create(RTLanguage.INSTANCE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new RTLexer();
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new RTParser();
    }

    @NotNull
    @Override
    public PsiElement createElement(ASTNode node) {
        final IElementType type = node.getElementType();
        if (type == RTElementTypes.REPEAT_EXPRESSION) {
            return new RTRepeatExpression(node);
        } else if (type == RTElementTypes.FILTER_EXPRESSION) {
            return new RTFilterExpression(node);
        } else if (type == RTElementTypes.AS_EXPRESSION) {
            return new RTAsExpression(node);
        }
        return super.createElement(node);
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }
}
