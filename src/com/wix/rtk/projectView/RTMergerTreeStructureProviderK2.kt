package com.wix.rtk.projectView

import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.BasePsiNode
import com.intellij.ide.projectView.impl.nodes.PsiFileNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import com.wix.rt.RTProjectComponent
import com.wix.rt.build.RTFileUtil
import com.wix.rt.settings.Settings
import java.util.*

object RTMergerTreeStructureProviderK2 {

    // private fun map(copy: Array<ProjectViewNode<Any>>): Map<String, ProjectViewNode<Any>> {
    //     val rtJsFiles = hashMapOf<String, ProjectViewNode<Any>>()
    //     copy.filter { isRTJS(it.virtualFile) }.forEach { rtJsFiles.put(it.virtualFile!!.name, it) }
    //     return rtJsFiles
    // }

    private fun isRTJS(file: VirtualFile?): Boolean = RTFileUtil.isRTJSFile(file) || hasJsExt(file) || hasTsExt(file)

    private fun hasRTFiles(children: Collection<AbstractTreeNode<*>>): Boolean = children.any { it is PsiFileNode && it.value is PsiFile && it.value.name.endsWith(".rt") }

    fun modify22(project: Project, parent: AbstractTreeNode<*>, children: Array<AbstractTreeNode<Any>>, settings: ViewSettings): Collection<AbstractTreeNode<*>> =
            modify(project, parent, children.asList(), settings)

    fun defaultSettings() = Settings()

    fun getSettings(project: Project): Settings {
        val comp = project.getComponent(RTProjectComponent::class.java)
        return comp?.settings ?: defaultSettings()
    }

    fun modify(project: Project, parent: AbstractTreeNode<*>, children: Collection<AbstractTreeNode<*>>, settings: ViewSettings): Collection<AbstractTreeNode<*>> {
        val sets = getSettings(project)
        if (!sets.pluginEnabled) {
            return children
        }
        if (parent is RTNode || parent.value is RTFile) {
            return children
        }

        // Optimization. Check if there are any RT files at all.
        val formsFound = hasRTFiles(children)
        if (!formsFound) {
            return children
        }

        val result = LinkedHashSet(children)
        val rtFiles = children.filter { it is PsiFileNode && it.value is PsiFile && it.value.name.endsWith(".rt") }

        fun toPsiFile(i: AbstractTreeNode<*>?): PsiFile? = if (i is PsiFileNode && i.value is PsiFile) i.value else null

        fun group(shouldGroup: (String, VirtualFile?) -> Boolean, subNodes: ArrayList<BasePsiNode<out PsiFile>>) {
            val sassFile = children.find { it is PsiFileNode && it.value is PsiFile && shouldGroup(it.value.name, toPsiFile(it)?.virtualFile) }
            if (sassFile != null) {
                subNodes.add(sassFile as BasePsiNode<out PsiFile>)
                result.remove(sassFile)
            }
        }

        fun fff(node: AbstractTreeNode<*>, file: VirtualFile?) = node is PsiFileNode && node.value is PsiFile && node.value.name == getSassName(file)

        // val toRemove = ArrayList<AbstractTreeNode<*>>()
        // remove all rt files and files with the same name
        fun f(it: AbstractTreeNode<*>): RTNode {
            //            val name = toPsiFile(it)?.name
            val file = toPsiFile(it)?.virtualFile
            //            val nameNoExt = toPsiFile(it)?.virtualFile?.nameWithoutExtension
            val subNodes = ArrayList<BasePsiNode<out PsiFile>>()
            subNodes.add(it as BasePsiNode<out PsiFile>)
            result.remove(it)
            val rtJs = children.find { it is PsiFileNode && it.value is PsiFile && it.value.name == file?.nameWithoutExtension + ".rt.js" }
            if (rtJs != null) {
                subNodes.add(rtJs as BasePsiNode<out PsiFile>)
                result.remove(rtJs)
            }
            val jsFile = children.find { it is PsiFileNode && it.value is PsiFile && it.value.name == getJSControllerName(file) }
            if (jsFile != null && sets.groupController) {
                subNodes.add(jsFile as BasePsiNode<out PsiFile>)
                result.remove(jsFile)
            }
            if (sets.groupOther) {
                val sassFile = children.find { it is PsiFileNode && it.value is PsiFile && it.value.name == getSassName(file) }
                if (sassFile != null) {
                    subNodes.add(sassFile as BasePsiNode<out PsiFile>)
                    result.remove(sassFile)
                }
            }
            val node = RTNode(project,
                    RTFile(toPsiFile(it)!!, toPsiFile(rtJs), toPsiFile(jsFile)),
                    settings,
                    subNodes
            )
            return node
        }

        val rtNodes = rtFiles.map { f(it) }

        return result + rtNodes
    }

    private fun hasExt(file: VirtualFile?, ext: String): Boolean = file?.extension == ext

    private fun hasJsExt(file: VirtualFile?): Boolean = hasExt(file, "js")

    private fun hasTsExt(file: VirtualFile?): Boolean = hasExt(file, "ts")

    private fun getJSControllerName(rtFile: VirtualFile?): String = rtFile?.nameWithoutExtension + ".js"

    private fun getJSRTName(rtFile: VirtualFile?): String = rtFile?.nameWithoutExtension + ".rt.js"

    private fun getSassName(rtFile: VirtualFile?): String = rtFile?.nameWithoutExtension + ".scss"

    private fun getTSControllerName(rtFile: VirtualFile?): String = rtFile?.nameWithoutExtension + ".ts"
}