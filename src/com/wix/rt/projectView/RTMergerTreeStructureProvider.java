package com.wix.rt.projectView;

import com.intellij.ide.*;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.SelectableTreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.projectView.impl.nodes.BasePsiNode;
import com.intellij.ide.util.DeleteHandler;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.containers.ContainerUtil;
import com.wix.rt.RTProjectComponent;
import com.wix.rt.build.RTFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RTMergerTreeStructureProvider implements SelectableTreeStructureProvider, DumbAware {
    private final Project project;

    public RTMergerTreeStructureProvider(Project project) {
        this.project = project;
    }

    private static boolean hasRTFiles(@NotNull Collection<AbstractTreeNode> children) {
        for (AbstractTreeNode node : children) {
            if (node.getValue() instanceof PsiFile) {
                PsiFile file = (PsiFile) node.getValue();
                if (file.getName().endsWith(".rt")) {
//                if (file.getFileType().equals(RTFileType.INSTANCE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasJsExt(VirtualFile file) {
        return file != null && file.getExtension() != null && file.getExtension().equals("js");
    }

    private static Map<String, ProjectViewNode> map(ProjectViewNode[] copy) {
        Map<String, ProjectViewNode> rtJsFiles = new HashMap<String, ProjectViewNode>();
        for (ProjectViewNode element : copy) {
            if (RTFileUtil.isRTJSFile(element.getVirtualFile()) || hasJsExt(element.getVirtualFile())) {
                rtJsFiles.put(element.getVirtualFile().getName(), element);
            }
        }
        return rtJsFiles;
    }

    private static boolean isGroupController(Project project) {
        RTProjectComponent comp = project.getComponent(RTProjectComponent.class);
        return comp.settings.groupController;
    }

    private static String getControllerName(String rtName) {
        return rtName.substring(0, rtName.length() - 2) + "js";
    }

    private static String getControllerName(VirtualFile rtFile) {
        return rtFile.getNameWithoutExtension() + ".js";
    }

    @NotNull
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent, @NotNull Collection<AbstractTreeNode> children, ViewSettings settings) {
        if (!RTProjectComponent.isEnabled(parent.getProject())) {
            return children;
        }
        if (parent.getValue() instanceof RTFile) return children;
        boolean groupController = isGroupController(project);
        RTProjectComponent comp = project.getComponent(RTProjectComponent.class);
        boolean groupOther = comp.settings.groupOther;

        // Optimization. Check if there are any forms at all.
        boolean formsFound = hasRTFiles(children);
        if (!formsFound) return children;

        Collection<AbstractTreeNode> result = new LinkedHashSet<AbstractTreeNode>(children);
        ProjectViewNode[] copy = children.toArray(new ProjectViewNode[children.size()]);

        Map<String, ProjectViewNode> rtJsFiles = map(copy);

        for (ProjectViewNode<?> element : copy) {
//            if (isRTJSFile(element.getVirtualFile())) {
//                //skip
//                continue;
//            }
            PsiFile psiClass = null;
            if (element.getValue() instanceof PsiFile) {
                psiClass = (PsiFile) element.getValue();
//            } else if (element.getValue() instanceof PsiClassOwner) {
//                final PsiClass[] psiClasses = ((PsiClassOwner) element.getValue()).getClasses();
//                if (psiClasses.length == 1) {
//                    psiClass = psiClasses[0];
//                }
            }
            if (psiClass == null) continue;
//            String qName = psiClass.getQualifiedName();
//            if (qName == null) continue;
//            List<PsiFile> forms = null;
            ProjectViewNode rtJsNode = null;
            try {
//                forms = FormClassIndex.findFormsBoundToClass(project, qName);
                String name = RTFileUtil.getJsRTFileName(element.getVirtualFile().getName());
//                VirtualFile form = element.getVirtualFile().getParent().findChild(name);
//                if (form != null) {
//                    forms = new ArrayList<PsiFile>();
                if (rtJsFiles.containsKey(name)) {
                    rtJsNode = rtJsFiles.get(name);
                }
//                    forms.add(rtFile);
//                }
            } catch (ProcessCanceledException e) {
                continue;
            }

            if (rtJsNode != null) {
                Collection<BasePsiNode<? extends PsiFile>> subNodes = new ArrayList<BasePsiNode<? extends PsiFile>>();
                //noinspection unchecked
                subNodes.add((BasePsiNode<? extends PsiFile>) element);
                //noinspection unchecked
                subNodes.add((BasePsiNode<? extends PsiFile>) rtJsNode);
                ProjectViewNode controllerNode = null;
                if (groupController) {
                    String name = getControllerName(element.getVirtualFile());
                    if (rtJsFiles.containsKey(name)) {
                        controllerNode = rtJsFiles.get(name);
                        subNodes.add((BasePsiNode<? extends PsiFile>) controllerNode);
                        result.remove(controllerNode);
                    }
                }
//                if (groupOther) {
//                    String name = getControllerName(element.getVirtualFile());
//                    if (rtJsFiles.containsKey(name)) {
//                        controllerNode = rtJsFiles.get(name);
//                        subNodes.add((BasePsiNode<? extends PsiFile>) controllerNode);
//                        result.remove(controllerNode);
//                    }
//                }
                PsiFile controller = controllerNode == null ? null : (PsiFile) controllerNode.getValue();
                result.add(new RTNode(project, new RTFile(psiClass, (PsiFile) rtJsNode.getValue(), controller), settings, subNodes));
                result.remove(element);
                result.remove(rtJsNode);
            }
//            Collection<BasePsiNode<? extends PsiElement>> formNodes = findFormsIn(children, forms);
//            if (!formNodes.isEmpty()) {
//                Collection<PsiFile> formFiles = convertToFiles(formNodes);
//                Collection<BasePsiNode<? extends PsiElement>> subNodes = new ArrayList<BasePsiNode<? extends PsiElement>>();
//                //noinspection unchecked
//                subNodes.add((BasePsiNode<? extends PsiElement>) element);
//                subNodes.addAll(formNodes);
//                result.add(new FormNode(project, new RTNode(rtFile, psiClass), settings, subNodes));
//                result.remove(element);
//                result.removeAll(formNodes);
//            }
        }

//        result.toArray()

        return result;
    }

    public Object getData(final Collection<AbstractTreeNode> selected, String dataId) {
        if (selected != null) {
            if (RTFile.DATA_KEY.is(dataId)) {
                List<RTFile> result = new ArrayList<RTFile>();
                for (AbstractTreeNode node : selected) {
                    if (node.getValue() instanceof RTFile) {
                        result.add((RTFile) node.getValue());
                    }
                }
                if (!result.isEmpty()) {
                    return result.toArray(new RTFile[result.size()]);
                }
            } else if (PlatformDataKeys.COPY_PROVIDER.is(dataId) || PlatformDataKeys.DELETE_ELEMENT_PROVIDER.is(dataId) || PlatformDataKeys.CUT_PROVIDER.is(dataId)) {
                for (AbstractTreeNode node : selected) {
                    if (node.getValue() instanceof RTFile) {
                        return new MyCopyProvider(selected);
                    }
                }
//            } else if (PlatformDataKeys.PASTE_PROVIDER.is(dataId)) {
            }
        }
        return null;
    }

    private static PsiElement[] collectFormPsiElements(Collection<AbstractTreeNode> selected) {
        Set<PsiElement> result = new HashSet<PsiElement>();
        for (AbstractTreeNode node : selected) {
            if (node.getValue() instanceof RTFile) {
                RTFile form = (RTFile) node.getValue();
                result.add(form.getRTJSFile());
                if (form.getController() != null) {
                    result.add(form.getController());
                }
                ContainerUtil.addAll(result, form.getRtFile());
            } else if (node.getValue() instanceof PsiElement) {
                result.add((PsiElement) node.getValue());
            }
        }
        return PsiUtilCore.toPsiElementArray(result);
    }

    private static Collection<PsiFile> convertToFiles(Collection<BasePsiNode<? extends PsiElement>> formNodes) {
        List<PsiFile> psiFiles = new ArrayList<PsiFile>();
        for (AbstractTreeNode treeNode : formNodes) {
            psiFiles.add((PsiFile) treeNode.getValue());
        }
        return psiFiles;
    }

    private static Collection<BasePsiNode<? extends PsiElement>> findFormsIn(Collection<AbstractTreeNode> children, List<PsiFile> forms) {
        if (children.isEmpty() || forms.isEmpty()) return Collections.emptyList();
        List<BasePsiNode<? extends PsiElement>> result = new ArrayList<BasePsiNode<? extends PsiElement>>();
        HashSet<PsiFile> psiFiles = new HashSet<PsiFile>(forms);
        for (final AbstractTreeNode child : children) {
            if (child instanceof BasePsiNode) {
                //noinspection unchecked
                BasePsiNode<? extends PsiElement> treeNode = (BasePsiNode<? extends PsiElement>) child;
                //noinspection SuspiciousMethodCalls
                if (psiFiles.contains(treeNode.getValue())) result.add(treeNode);
            }
        }
        return result;
    }

    @Nullable
    @Override
    public PsiElement getTopLevelElement(PsiElement psiElement) {
        return null;
    }

    private static class MyCopyProvider implements CopyProvider, PasteProvider, CutProvider, DeleteProvider {
        private final PsiElement[] myElements;

        public MyCopyProvider(final Collection<AbstractTreeNode> selected) {
            myElements = collectFormPsiElements(selected);
        }

        private static PsiElement[] collectFormPsiElements(Collection<AbstractTreeNode> selected) {
            Set<PsiElement> result = new HashSet<PsiElement>();
            for (AbstractTreeNode node : selected) {
                if (node.getValue() instanceof RTFile) {
                    RTFile form = (RTFile) node.getValue();
                    result.add(form.getRTJSFile());
                    if (form.getController() != null) {
                        result.add(form.getController());
                    }
                    ContainerUtil.addAll(result, form.getRtFile());
                } else if (node.getValue() instanceof PsiElement) {
                    result.add((PsiElement) node.getValue());
                }
            }
            return PsiUtilCore.toPsiElementArray(result);
        }

        @Override
        public void performCopy(@NotNull DataContext dataContext) {
//            final SerializedComponentData data = new SerializedComponentData(serializeForCopy(myEditor, selectedComponents));
//            final SimpleTransferable transferable = new SimpleTransferable<SerializedComponentData>(data, SerializedComponentData.class, ourDataFlavor);
//            CopyPasteManager.getInstance().setContents();
            PsiCopyPasteManager.getInstance().setElements(myElements, true);
        }

        @Override
        public boolean isCopyEnabled(@NotNull DataContext dataContext) {
            return true;
        }

        @Override
        public boolean isCopyVisible(@NotNull DataContext dataContext) {
            return true;
        }

        @Override
        public void performPaste(@NotNull DataContext dataContext) {
            RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
            if (rtFiles != null) {
                System.out.println(rtFiles.length);
            }
        }

        @Override
        public boolean isPastePossible(@NotNull DataContext dataContext) {
            return true;
        }

        @Override
        public boolean isPasteEnabled(@NotNull DataContext dataContext) {
            return true;
        }

        @Override
        public void performCut(@NotNull DataContext dataContext) {
            PsiCopyPasteManager.getInstance().setElements(myElements, true);
        }

        @Override
        public boolean isCutEnabled(@NotNull DataContext dataContext) {
            return true;
        }

        @Override
        public boolean isCutVisible(@NotNull DataContext dataContext) {
            return true;
        }

        public void deleteElement(@NotNull DataContext dataContext) {
            Project project = CommonDataKeys.PROJECT.getData(dataContext);
            DeleteHandler.deletePsiElement(myElements, project);
        }

        public boolean canDeleteElement(@NotNull DataContext dataContext) {
            return DeleteHandler.shouldEnableDeleteAction(myElements);
        }
    }
}
