package com.wix.rt.lang.psi;

import com.intellij.psi.PsiElement;

/**
 * @author Dennis.Ushakov
 */
public class RTRecursiveVisitor extends RTElementVisitor {
    @Override
    public void visitElement(PsiElement element) {
        element.acceptChildren(this);
    }
}