package com.wix.rt.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.wix.rt.build.RTFileListener;
import com.wix.rt.build.RTFileUtil;
import com.wix.rt.projectView.RTFile;
import com.wix.rt.projectView.RTMergerTreeStructureProvider;
import org.jetbrains.annotations.NotNull;

/**
 * build react templates action
 * Created by idok on 11/17/14.
 */
public class SwitchViewAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enabled = false;
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        boolean rtEnabled = RTActionUtil.isRTEnabled(project);
        if (project != null) {
            final VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
            enabled = rtEnabled && (RTFileUtil.isRTFile(file) || BuildTemplateAction.isRtFileContext(e.getDataContext()));
//            if (file != null) {
//                e.getPresentation().setText("Switch to Code Behind file '" + file.getName() + '\'');
//            }
        }
        e.getPresentation().setVisible(enabled);
    }

    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) return;
        final VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);

        if (file == null) {
            RTFile[] rtFiles = RTFile.DATA_KEY.getData(e.getDataContext());
            if (rtFiles == null || rtFiles.length == 0) {
                System.out.println("No file for rt compile");
                return;
            }
            // handle all files
            for (RTFile rtFile : rtFiles) {
                FileEditorManager.getInstance(project).openFile(rtFile.getController().getVirtualFile(), true, true);
            }
        } else {
            VirtualFile vfs = file.getParent().findChild(RTMergerTreeStructureProvider.getJSControllerName(file));
            if (vfs != null) {
                FileEditorManager.getInstance(project).openFile(vfs, true, true);
            }
        }
    }
}
