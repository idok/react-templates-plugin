package com.wix.rtk.projectView

import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiFile

/**
 * RT file node
 */
data class RTFile(val rtFile: PsiFile?, val rtjsFile: PsiFile?, val controller: PsiFile?) : Navigatable {
    val name: String
        get() = rtFile?.virtualFile?.nameWithoutExtension ?: ""

    override fun navigate(requestFocus: Boolean) {
        if (rtFile != null && rtFile.canNavigate()) {
            rtFile.navigate(requestFocus)
        }
    }

    override fun canNavigateToSource(): Boolean = bool(rtFile?.canNavigateToSource())

    override fun canNavigate(): Boolean = bool(rtFile?.canNavigate())

    val isValid: Boolean
        get() = bool(rtFile?.isValid) && bool(rtjsFile?.isValid) && bool(controller?.isValid)

    fun containsFile(vFile: VirtualFile): Boolean {
        val classFile = rtjsFile?.containingFile
        val classVFile = classFile?.virtualFile
        if (classVFile != null && classVFile == vFile) {
            return true
        }
        val virtualFile = rtFile?.virtualFile
        return virtualFile != null && virtualFile == vFile
    }

    companion object {
        val DATA_KEY = DataKey.create<Array<RTFile>>("rt.array")
        private fun areEqual(a: Any?, b: Any?): Boolean = if (a == null) b == null else a == b
    }
}

fun bool(v: Boolean?, d: Boolean = false) = v ?: d
fun Boolean?.bool1(d: Boolean = false) = this ?: d

fun f() {
    val b:Boolean? = true
    val r = b.bool1()
}
