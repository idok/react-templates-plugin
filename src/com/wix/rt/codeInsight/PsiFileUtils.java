package com.wix.rt.codeInsight;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.psi.PsiFile;

public final class PsiFileUtils {

//    public static final String HTML_EXT = ".html";

    public static boolean isHtmlFile(PsiFile psiFile) {
        return psiFile.getFileType().equals(StdFileTypes.HTML);
    }

    private PsiFileUtils() {
    }
}
