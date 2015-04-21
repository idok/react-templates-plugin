package com.wix.rt.lang.psi;

import com.intellij.lang.javascript.psi.JSElementVisitor;

/**
 * @author Dennis.Ushakov
 */
public class RTElementVisitor extends JSElementVisitor {
    public void visitRTRepeatExpression(RTRepeatExpression repeatExpression) {
        visitJSExpression(repeatExpression);
    }

    public void visitRTAsExpression(RTAsExpression asExpression) {
        visitJSExpression(asExpression);
    }
}
