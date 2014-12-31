package com.wix.rt.projectView;

import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;

public class RTFile implements Navigatable {
    public static final DataKey<RTFile[]> DATA_KEY = DataKey.create("rt.array");
//    public static final DataKey<RTFile> DATA_KEY = DataKey.create("rt.file");

    //    private final Collection<PsiFile> myFormFiles;
    private final PsiFile rtFile;
    private final PsiFile rtJSFile;

    public RTFile(PsiFile rtFile, PsiFile rtJSFile) {
        this.rtFile = rtFile;
        this.rtJSFile = rtJSFile;
//        myFormFiles = FormClassIndex.findFormsBoundToClass(classToBind);
    }

//    public RTNode(PsiClass classToBind, Collection<PsiFile> formFiles) {
//        rtJSFile = classToBind;
//        myFormFiles = new HashSet<PsiFile>(formFiles);
//    }

    public boolean equals(Object object) {
        if (object instanceof RTFile) {
            RTFile form = (RTFile) object;
            return rtFile.equals(form.rtFile) && rtJSFile.equals(form.rtJSFile);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return rtFile.hashCode() ^ rtJSFile.hashCode();
    }

    public String getName() {
        return rtFile.getName();
    }

    public PsiFile getRTJSFile() {
        return rtJSFile;
    }

//    public PsiFile[] getFormFiles() {
//        return PsiUtilCore.toPsiFileArray(rtFile);
//    }

    public PsiFile getRtFile() {
        return rtFile;
    }

    public void navigate(boolean requestFocus) {
//        for (PsiFile psiFile : myFormFiles) {
//            if (psiFile != null && psiFile.canNavigate()) {
//                psiFile.navigate(requestFocus);
//            }
//        }
        if (rtFile != null && rtFile.canNavigate()) {
            rtFile.navigate(requestFocus);
        }
    }

    public boolean canNavigateToSource() {
//        for (PsiFile psiFile : myFormFiles) {
//            if (psiFile != null && psiFile.canNavigateToSource()) return true;
//        }
        return rtFile != null && rtFile.canNavigateToSource();
    }

    public boolean canNavigate() {
//        for (PsiFile psiFile : myFormFiles) {
//            if (psiFile != null && psiFile.canNavigate()) return true;
//        }
        return rtFile != null && rtFile.canNavigate();
    }

    public boolean isValid() {
//        if (myFormFiles.size() == 0) return false;
//        for (PsiFile psiFile : myFormFiles) {
//            if (!psiFile.isValid()) {
//                return false;
//            }
//        }
        return rtFile.isValid() && rtJSFile.isValid();
    }

    public boolean containsFile(final VirtualFile vFile) {
        final PsiFile classFile = rtJSFile.getContainingFile();
        final VirtualFile classVFile = classFile == null ? null : classFile.getVirtualFile();
        if (classVFile != null && classVFile.equals(vFile)) {
            return true;
        }
//        for (PsiFile psiFile : myFormFiles) {
//            final VirtualFile virtualFile = psiFile.getVirtualFile();
//            if (virtualFile != null && virtualFile.equals(vFile)) {
//                return true;
//            }
//        }
        final VirtualFile virtualFile = rtFile.getVirtualFile();
        return virtualFile != null && virtualFile.equals(vFile);
    }

//    public static boolean hasData(DataContext dataContext) {
//        RTFile forms = RTFile.DATA_KEY.getData(dataContext);
//        return forms != null;
//    }
}
