package com.wix.rtk.projectView

import com.intellij.ide.projectView.PresentationData
import com.intellij.ide.projectView.ProjectViewNode
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.BasePsiNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.navigation.NavigationItemFileStatus
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.wix.rt.build.RTFileType
import icons.RTIcons

class RTNode(project: Project, value: RTFile, viewSettings: ViewSettings, private val children: Collection<BasePsiNode<out PsiFile>>) : ProjectViewNode<RTFile>(project, value, viewSettings) {

    override fun getChildren(): Collection<BasePsiNode<out PsiFile>> = children

    override fun getWeight(): Int = 20

    override fun getTypeSortKey(): Comparable<*>? = PsiFileNode.ExtensionSortKey(RTFileType.RT_EXT)

    override fun getTestPresentation(): String? = "RTNode:${value.name}"

    override fun contains(file: VirtualFile): Boolean = children.any { it is ProjectViewNode<*> && it.contains(file) }

    public override fun update(presentation: PresentationData) {
        if (value == null || !value.isValid) {
            value = null
        } else {
            presentation.presentableText = value.name
            presentation.setIcon(RTIcons.RT)
        }
    }

    override fun navigate(requestFocus: Boolean) {
        value.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean = value?.canNavigate() ?: false

    override fun canNavigateToSource(): Boolean = value?.canNavigateToSource() ?: false

    public override fun getToolTip(): String = "React Templates"

    override fun getFileStatus(): FileStatus = children.filter { it.isValid }.map { NavigationItemFileStatus.get(it) }.find { it != FileStatus.NOT_CHANGED } ?: FileStatus.NOT_CHANGED

    override fun canHaveChildrenMatching(condition: Condition<PsiFile>?): Boolean = children.any { condition!!.value(it.value.containingFile) }

    override fun getVirtualFile(): VirtualFile? = value?.rtFile?.virtualFile
}