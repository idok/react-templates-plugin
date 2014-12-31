package com.wix.rt.build;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression;
import com.intellij.lang.javascript.psi.JSProperty;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author idok
 */
public final class RTFileUtil {
    private RTFileUtil() {
    }

    public static boolean isRTJSFile(VirtualFile file) {
        return file.getName().endsWith(".rt.js");
    }

    public static boolean hasRTExt(VirtualFile file) {
        return file != null && file.getName().endsWith(".rt");
    }

    public static boolean hasRTExt(PsiFile file) {
        return hasRTExt(file.getVirtualFile());
    }

    public static boolean isRTFile(JSFile file) {
        return file != null && /*isRTFile(file.getVirtualFile()) ||*/ file.getFileType().equals(RTFileType.INSTANCE);
    }

    public static String getJsRTFileName(String rtFileName) {
        return rtFileName + ".js";
    }

    public static String getJsRTFileName(VirtualFile file) {
        return getJsRTFileName(file.getName());
    }

//    public static boolean isRTFile(PsiElement position) {
//        return isRTFile(position.getContainingFile().getOriginalFile().getVirtualFile());
//    }
//
    public static boolean isRTFile(VirtualFile file) {
        return file != null && file.getFileType().equals(RTFileType.INSTANCE); // file.getName().equals(RTFileType.ESLINTRC);
    }

    public static boolean isRTFile(PsiFile file) {
//        return file != null && file.getFileType().equals(RTFileType.INSTANCE); for some reason file type is html, so we'll test ext
        return file != null && hasRTExt(file.getVirtualFile());
    }

    @Nullable
    public static JSProperty getProperty(@NotNull PsiElement position) {
        JSProperty property = PsiTreeUtil.getParentOfType(position, JSProperty.class, false);
        if (property != null) {
            JSObjectLiteralExpression objectLiteralExpression = ObjectUtils.tryCast(property.getParent(), JSObjectLiteralExpression.class);
            if (objectLiteralExpression != null) {
                return property;
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement getStringLiteral(@NotNull JSProperty property) {
        PsiElement firstElement = property.getFirstChild();
        if (firstElement != null && isStringLiteral(firstElement)) {
            return firstElement;
        }
        return null;
    }

    public static boolean isStringLiteral(@NotNull PsiElement element) {
        if (element instanceof ASTNode) {
            ASTNode node = (ASTNode) element;
            return node.getElementType().equals(JSTokenTypes.STRING_LITERAL);
        }
        return false;
    }
}
