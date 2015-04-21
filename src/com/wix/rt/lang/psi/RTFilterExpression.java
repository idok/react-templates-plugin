package com.wix.rt.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.lang.javascript.psi.impl.JSCallExpressionImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

/**
 * @author Dennis.Ushakov
 */
public class RTFilterExpression extends JSCallExpressionImpl {
    public RTFilterExpression(ASTNode node) {
        super(node);
    }

    public static boolean isFilterNameRef(PsiReference ref, PsiElement parent) {
        return parent instanceof RTFilterExpression && ((RTFilterExpression) parent).getFilterName() == ref;
    }

    public JSReferenceExpression getFilterName() {
        return (JSReferenceExpression) getFirstChild();
    }
}
