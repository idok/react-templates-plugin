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

/*
* Created by IntelliJ IDEA.
* User: yole
* Date: 26.10.2006
* Time: 16:44:00
*/
package com.wix.rt.projectView;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandlerFactory;
import com.intellij.refactoring.rename.RenameHandler;
import com.intellij.refactoring.rename.RenameProcessor;
import org.jetbrains.annotations.NotNull;

public class RTRenameHandler implements RenameHandler {
    public boolean isAvailableOnDataContext(DataContext dataContext) {
        RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
        return rtFiles != null && rtFiles.length == 1;
    }

    public boolean isRenaming(DataContext dataContext) {
        return isAvailableOnDataContext(dataContext);
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext) {
        RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
        if (rtFiles == null || rtFiles.length != 1) return;
        PsiFile rtJsFile = rtFiles[0].getRTJSFile();
        PsiFile rtFile = rtFiles[0].getRtFile();
        RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, new PsiElement[]{rtFile}, dataContext);
//        if (rtJsFile != null) {
//            RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, new PsiElement[]{rtFile}, dataContext);
//        } else {
//            RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, new PsiElement[]{rtJsFile}, dataContext);
//        }
    }

    public void invoke(@NotNull Project project, @NotNull PsiElement[] elements, DataContext dataContext) {
        invoke(project, null, null, dataContext);
    }

//    private boolean doRename(@NotNull Project project, Editor editor, PsiFile file, DataContext dataContext, PsiFile rtJsFile) {
////        final List<PropertiesFile> propertiesFiles = myResourceBundle.getPropertiesFiles(myProject);
////        for (PropertiesFile propertiesFile : propertiesFiles) {
//            if (!FileModificationService.getInstance().prepareFileForWrite(rtJsFile.getContainingFile()))
//                return false;
////        }
//
//        RenameProcessor renameProcessor = null;
////        String baseName = myResourceBundle.getBaseName();
////        for (PropertiesFile propertiesFile : propertiesFiles) {
//            final VirtualFile virtualFile = rtJsFile.getVirtualFile();
//            if (virtualFile == null) {
//                return false;
//            }
//            final String newName = inputString + virtualFile.getNameWithoutExtension().substring(baseName.length()) + "."
//                    + virtualFile.getExtension();
//            if (renameProcessor == null) {
//                renameProcessor = new RenameProcessor(project, rtJsFile.getContainingFile(), newName, false, false);
//                return false;
//            }
//            renameProcessor.addElement(propertiesFile.getContainingFile(), newName);
////        }
//        if (renameProcessor == null) {
//            LOG.assertTrue(false);
//            return true;
//        }
//        renameProcessor.setCommandName(PropertiesBundle.message("rename.resource.bundle.dialog.title"));
//        renameProcessor.doRun();
//        return true;
//    }
}