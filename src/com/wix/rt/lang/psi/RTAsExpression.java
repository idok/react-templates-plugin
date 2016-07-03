package com.wix.rt.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.psi.JSDefinitionExpression;
import com.intellij.lang.javascript.psi.impl.JSBinaryExpressionImpl;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Dennis.Ushakov
 */
public class RTAsExpression extends JSBinaryExpressionImpl {
    public RTAsExpression(ASTNode node) {
        super(node);
    }

    @Nullable
    public JSDefinitionExpression getDefinition() {
        return PsiTreeUtil.getChildOfType(this, JSDefinitionExpression.class);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof RTElementVisitor) {
            ((RTElementVisitor) visitor).visitRTAsExpression(this);
        } else {
            super.accept(visitor);
        }
    }

//    public static boolean isAsControllerRef(PsiReference ref, PsiElement parent) {
//        if (parent instanceof RTAsExpression && ref == parent.getFirstChild()) {
//            return true;
//        }
//        final InjectedLanguageManager injector = InjectedLanguageManager.getInstance(parent.getProject());
//        final PsiLanguageInjectionHost host = injector.getInjectionHost(parent);
//        final PsiElement hostParent = host instanceof XmlAttributeValueImpl ? host.getParent() : null;
//        final String normalized = hostParent instanceof XmlAttribute ?
//                DirectiveUtil.normalizeAttributeName(((XmlAttribute) hostParent).getName()) : null;
//        return "ng-controller".equals(normalized);
//    }
}
