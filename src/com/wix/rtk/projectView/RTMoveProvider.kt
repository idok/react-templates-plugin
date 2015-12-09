package com.wix.rtk.projectView

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.refactoring.move.MoveHandlerDelegate
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFilesOrDirectoriesHandler
import com.wix.rt.build.RTFileUtil
import com.wix.rt.projectView.RTFile

/**
 * @author yole
 */
class RTMoveProvider : MoveHandlerDelegate() {

    override fun canMove(dataContext: DataContext?): Boolean = RTFile.DATA_KEY.getData(dataContext!!)?.size ?: 0 > 0

    override fun isValidTarget(psiElement: PsiElement?, sources: Array<PsiElement>?): Boolean = MoveFilesOrDirectoriesHandler.isValidTarget(psiElement)

    override fun canMove(elements: Array<PsiElement>, targetContainer: PsiElement?): Boolean = false

    override fun collectFilesOrDirsFromContext(dataContext: DataContext?, filesOrDirs: MutableSet<PsiElement>?) {
        val rtFiles = RTFile.DATA_KEY.getData(dataContext!!)
        LOG.assertTrue(rtFiles != null)
        if (rtFiles?.size == 0) return
        val jsToMove = arrayOfNulls<PsiFile>(rtFiles!!.size)
        val filesToMove = arrayOfNulls<PsiFile>(rtFiles.size)
        val controllersToMove = arrayOfNulls<PsiFile>(rtFiles.size)
        for (i in rtFiles.indices) {
            jsToMove[i] = rtFiles[i].rtjsFile
            if (jsToMove[i] != null) {
                filesOrDirs!!.add(jsToMove[i]?.containingFile!!)
            }
            filesToMove[i] = rtFiles[i].rtFile
            if (filesToMove[i] != null) {
                filesOrDirs!!.add(filesToMove[i]!!)
            }
            controllersToMove[i] = rtFiles[i].controller
            if (filesToMove[i] != null) {
                filesOrDirs!!.add(controllersToMove[i]!!)
            }
        }
    }

    override fun isMoveRedundant(source: PsiElement?, target: PsiElement?): Boolean = source is PsiFile && source.parent == target && RTFileUtil.isRTFile(source.virtualFile) || super.isMoveRedundant(source, target)

    companion object {
        private val LOG = Logger.getInstance("#com.wix.rt.projectView.RTMoveProvider")
    }
}
