package com.wix.rt.lang;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightInfoFilter;
import com.intellij.psi.PsiFile;

/**
 * Created by idok on 4/22/15.
 */
public class RTInfoFilter implements HighlightInfoFilter {
    private static final String MSG_EMPTY_TAG = "Empty tag doesn't work in some browsers";

    @Override
    public boolean accept(HighlightInfo highlightInfo, PsiFile psiFile) {
        if (psiFile != null && psiFile.getName().endsWith(".rt") && MSG_EMPTY_TAG.equals(highlightInfo.getDescription())) {
            return false;
        }
//        System.out.println(highlightInfo.getSeverity() + " " + highlightInfo.getDescription());
        return true;
    }
}
