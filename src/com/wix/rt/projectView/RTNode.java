package com.wix.rt.projectView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.navigation.NavigationItemFileStatus;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.wix.rt.build.RTFileType;
import icons.RTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class RTNode extends ProjectViewNode<RTFile> {
    public RTNode(Project project, RTFile value, ViewSettings viewSettings, Collection<BasePsiNode<? extends PsiFile>> children) {
        super(project, value, viewSettings);
        this.children = children;
    }

    private final Collection<BasePsiNode<? extends PsiFile>> children;

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
        for (final BasePsiNode<? extends PsiFile> child : children) {
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
        return "React Templates";
    }

    @Override
    public FileStatus getFileStatus() {
        for (BasePsiNode<? extends PsiFile> child : children) {
            final PsiFile value = child.getValue();
            if (value == null || !value.isValid()) continue;
            final FileStatus fileStatus = NavigationItemFileStatus.get(child);
            if (!fileStatus.equals(FileStatus.NOT_CHANGED)) {
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

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return getValue().getRtFile().getVirtualFile();
    }
}
