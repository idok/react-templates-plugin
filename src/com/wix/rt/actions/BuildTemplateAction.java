package com.wix.rt.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.wix.rt.build.RTFileListener;
import com.wix.rt.build.RTFileUtil;
import com.wix.rt.projectView.RTFile;
import org.jetbrains.annotations.NotNull;

/**
 * build react templates action
 * Created by idok on 11/17/14.
 */
public class BuildTemplateAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean enabled = false;
        Project project = CommonDataKeys.PROJECT.getData(e.getDataContext());
        boolean rtEnabled = RTActionUtil.isRTEnabled(project);
        if (project != null) {
            final VirtualFile file = (VirtualFile) e.getDataContext().getData(DataConstants.VIRTUAL_FILE);
            enabled = rtEnabled && (RTFileUtil.isRTFile(file) || isRtFileContext(e.getDataContext()));
            if (file != null) {
                e.getPresentation().setText("Build React Template '" + file.getName() + '\'');
            }
        }
        e.getPresentation().setVisible(enabled);
    }

    private static boolean isRtFileContext(DataContext dataContext) {
        RTFile[] rtFiles = RTFile.DATA_KEY.getData(dataContext);
        return rtFiles != null && rtFiles.length > 0;
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
                RTFileListener.compile(rtFile.getRtFile().getVirtualFile(), project);
            }
        } else {
            RTFileListener.compile(file, project);
        }
    }

    public static void info(Project project, String msg) {
        Notification errorNotification1 = new Notification("React-Templates plugin", "React-Templates plugin", msg, NotificationType.INFORMATION);
        Notifications.Bus.notify(errorNotification1, project);
    }

    public static void warn(Project project, String msg) {
        Notification errorNotification1 = new Notification("React-Templates plugin", "React-Templates plugin", msg, NotificationType.WARNING);
        Notifications.Bus.notify(errorNotification1, project);
    }
}
