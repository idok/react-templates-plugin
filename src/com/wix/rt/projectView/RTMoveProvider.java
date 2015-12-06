/*
* Copyright 2000-2009 JetBrains s.r.o.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.wix.rt.projectView;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.refactoring.move.MoveHandlerDelegate;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesHandler;
import com.wix.rt.build.RTFileType;
import com.wix.rt.build.RTFileUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author yole
 */
public class RTMoveProvider extends MoveHandlerDelegate {
    private static final Logger LOG = Logger.getInstance("#com.wix.rt.projectView.RTMoveProvider");

    @Override
    public boolean canMove(DataContext dataContext) {
        RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
        return rtFiles != null && rtFiles.length > 0;
    }

    @Override
    public boolean isValidTarget(PsiElement psiElement, PsiElement[] sources) {
        return MoveFilesOrDirectoriesHandler.isValidTarget(psiElement);
    }

    public boolean canMove(PsiElement[] elements, @Nullable final PsiElement targetContainer) {
        return false;
    }

    @Override
    public void collectFilesOrDirsFromContext(DataContext dataContext, Set<PsiElement> filesOrDirs) {
        RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
        LOG.assertTrue(rtFiles != null);
        if (rtFiles.length == 0) return;
        PsiFile[] jsToMove = new PsiFile[rtFiles.length];
        PsiFile[] filesToMove = new PsiFile[rtFiles.length];
        PsiFile[] controllersToMove = new PsiFile[rtFiles.length];
        for (int i = 0; i < rtFiles.length; i++) {
            jsToMove[i] = rtFiles[i].getRTJSFile();
            if (jsToMove[i] != null) {
                filesOrDirs.add(jsToMove[i].getContainingFile());
            }
            filesToMove[i] = rtFiles[i].getRtFile();
            if (filesToMove[i] != null) {
                filesOrDirs.add(filesToMove[i]);
            }
            controllersToMove[i] = rtFiles[i].getController();
            if (filesToMove[i] != null) {
                filesOrDirs.add(controllersToMove[i]);
            }
        }
    }

    @Override
    public boolean isMoveRedundant(PsiElement source, PsiElement target) {
        if (source instanceof PsiFile && source.getParent().equals(target)) {
            final VirtualFile virtualFile = ((PsiFile) source).getVirtualFile();
            if (RTFileUtil.isRTFile(virtualFile)) {
                return true;
            }
        }
        return super.isMoveRedundant(source, target);
    }
}
