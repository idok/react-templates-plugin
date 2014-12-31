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

import com.intellij.ide.IdeBundle;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.navigation.NavigationItemFileStatus;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.wix.rt.build.RTFileType;
import icons.RTIcons;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class RTNode extends ProjectViewNode<RTFile> {
    private final Collection<BasePsiNode<? extends PsiFile>> children;

    public RTNode(Project project, Object value, ViewSettings viewSettings) {
        this(project, (RTFile) value, viewSettings, getChildren(project, (RTFile) value, viewSettings));
    }

    public RTNode(Project project, RTFile value, ViewSettings viewSettings, Collection<BasePsiNode<? extends PsiFile>> children) {
        super(project, value, viewSettings);
        this.children = children;
    }

    @NotNull
    public Collection<BasePsiNode<? extends PsiFile>> getChildren() {
        return children;
    }

    @Override
    public int getWeight() {
        return 20;
    }

    @Override
    public Comparable getTypeSortKey() {
        return new PsiFileNode.ExtensionSortKey(RTFileType.INSTANCE.getDefaultExtension());
    }

    public String getTestPresentation() {
        return "RTNode:" + getValue().getName();
    }

    public boolean contains(@NotNull VirtualFile file) {
        for (final AbstractTreeNode child : children) {
            ProjectViewNode treeNode = (ProjectViewNode) child;
            if (treeNode.contains(file)) return true;
        }
        return false;
    }

    public void update(PresentationData presentation) {
        if (getValue() == null || !getValue().isValid()) {
            setValue(null);
        } else {
            presentation.setPresentableText(getValue().getName());
            presentation.setIcon(RTIcons.RT);
        }
    }

    public void navigate(final boolean requestFocus) {
        getValue().navigate(requestFocus);
    }

    public boolean canNavigate() {
        final RTFile value = getValue();
        return value != null && value.canNavigate();
    }

    public boolean canNavigateToSource() {
        final RTFile value = getValue();
        return value != null && value.canNavigateToSource();
    }

    public String getToolTip() {
        return IdeBundle.message("tooltip.ui.designer.form");
    }

    @Override
    public FileStatus getFileStatus() {
        for (BasePsiNode<? extends PsiFile> child : children) {
            final PsiFile value = child.getValue();
            if (value == null || !value.isValid()) continue;
            final FileStatus fileStatus = NavigationItemFileStatus.get(child);
            if (fileStatus != FileStatus.NOT_CHANGED) {
                return fileStatus;
            }
        }
        return FileStatus.NOT_CHANGED;
    }

    @Override
    public boolean canHaveChildrenMatching(final Condition<PsiFile> condition) {
        for (BasePsiNode<? extends PsiFile> child : children) {
            if (condition.value(child.getValue().getContainingFile())) {
                return true;
            }
        }
        return false;
    }

    public static AbstractTreeNode constructFormNode(final PsiFile rtJSFile, final Project project, final ViewSettings settings) {
        final RTFile form = new RTFile(null, rtJSFile);
        final Collection<BasePsiNode<? extends PsiFile>> children = getChildren(project, form, settings);
        return new RTNode(project, form, settings, children);
    }

    private static Collection<BasePsiNode<? extends PsiFile>> getChildren(final Project project, final RTFile form, final ViewSettings settings) {
        final Set<BasePsiNode<? extends PsiFile>> children = new LinkedHashSet<BasePsiNode<? extends PsiFile>>();
//        children.add(new ClassTreeNode(project, form.getRTJSFile(), settings));
        children.add(new PsiFileNode(project, form.getRTJSFile(), settings));
//        for (PsiFile formBoundToClass : form.getFormFiles()) {
//            children.add(new PsiFileNode(project, formBoundToClass, settings));
//        }
        children.add(new PsiFileNode(project, form.getRtFile(), settings));
        return children;
    }
}
