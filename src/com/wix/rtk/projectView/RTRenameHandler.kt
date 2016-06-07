package com.wix.rtk.projectView

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.RefactoringActionHandlerFactory
import com.intellij.refactoring.rename.RenameHandler

class RTRenameHandler : RenameHandler {
    override fun isAvailableOnDataContext(dataContext: DataContext): Boolean {
        val rtFiles = RTFile.DATA_KEY.getData(dataContext)
        return rtFiles?.size == 1
    }

    override fun isRenaming(dataContext: DataContext): Boolean = isAvailableOnDataContext(dataContext)

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?, dataContext: DataContext) {
        val rtFiles = RTFile.DATA_KEY.getData(dataContext)
        if (rtFiles == null || rtFiles.size != 1) return
//        val rtJsFile = rtFiles[0].rtjsFile
        val rtFile = rtFiles[0].rtFile
        RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, arrayOf<PsiElement>(rtFile), dataContext)
//        if (rtJsFile != null) {
//            RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, new PsiElement[]{ rtFile }, dataContext);
//        } else {
//            RefactoringActionHandlerFactory.getInstance().createRenameHandler().invoke(project, new PsiElement[]{ rtJsFile }, dataContext);
//        }
    }

    override fun invoke(project: Project, elements: Array<PsiElement>, dataContext: DataContext) {
        invoke(project, null, null, dataContext)
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