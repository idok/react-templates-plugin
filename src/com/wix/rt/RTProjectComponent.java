package com.wix.rt;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.wix.rt.build.RTFileListener;
import com.wix.rt.settings.Settings;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

public class RTProjectComponent implements ProjectComponent {
    public static final String FIX_CONFIG_HREF = "\n<a href=\"#\">Fix Configuration</a>";
    protected Project project;
    public Settings settings;
    protected boolean settingValidStatus;
    protected String settingValidVersion;
    protected String settingVersionLastShowNotification;

    private static final Logger LOG = Logger.getInstance(RTBundle.LOG_ID);

    public String rtExecutable;
    public String nodeInterpreter;
    public boolean treatAsWarnings;
    public boolean pluginEnabled;

    public static final String PLUGIN_NAME = "React-Templates plugin";

    public RTProjectComponent(Project project) {
        this.project = project;
        settings = Settings.getInstance(project);
    }

    @Override
    public void projectOpened() {
        if (isEnabled()) {
            isSettingsValid();
        }

        // TODO compile all rt when project starts if enabled, or when enabled
//        StartupManager.getInstance(project).runWhenProjectIsInitialized(new DumbAwareRunnable() {
//            public void run() {
//                ApplicationManager.getApplication().invokeLater(new Runnable() {
//                    public void run() {
//                        ApplicationManager.getApplication().runWriteAction(new Runnable() {
//                            public void run() {
//                                createProject(project, contentRoot);
//                            }
//                        });
//                    }
//                });
//            }
//        });
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
        if (isEnabled()) {
            isSettingsValid();
            RTFileListener.start(project);
        }
    }

    @Override
    public void disposeComponent() {
        // TODO
        //VirtualFileManager.getInstance().removeVirtualFileListener(virtualFileListener);
        RTFileListener.stop(project);
    }

    public static RTProjectComponent getInstace(Project project) {
        return project.getComponent(RTProjectComponent.class);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "RTProjectComponent";
    }

    public boolean isEnabled() {
        return Settings.getInstance(project).pluginEnabled;
    }

    public static boolean isEnabled(@NotNull final Project project) {
        RTProjectComponent component = project.getComponent(RTProjectComponent.class);
        return component.isEnabled();
    }

    public boolean isSettingsValid() {
        if (!settings.getVersion().equals(settingValidVersion)) {
            validateSettings();
            settingValidVersion = settings.getVersion();
        }
        return settingValidStatus;
    }

    public boolean validateSettings() {
        // do not validate if disabled
        if (!settings.pluginEnabled) {
            RTFileListener.stop(project);
            return true;
        }
        boolean status = validateField("Node Interpreter", settings.nodeInterpreter, true, false, true);
        if (!status) {
            return false;
        }
//        status = validateField("Rules", settings.rulesPath, false, true, false);
//        if (!status) {
//            return false;
//        }
        status = validateField("React-Templates bin", settings.rtExecutable, false, false, true);
        if (!status) {
            return false;
        }
        status = validateField("Builtin rules", settings.builtinRulesPath, false, true, false);
        if (!status) {
            return false;
        }

//        if (StringUtil.isNotEmpty(settings.rtExecutable)) {
//            File file = new File(project.getBasePath(), settings.rtExecutable);
//            if (!file.exists()) {
//                showErrorConfigNotification(ESLintBundle.message("eslint.rules.dir.does.not.exist", file.toString()));
//                LOG.debug("Rules directory not found");
//                settingValidStatus = false;
//                return false;
//            }
//        }
        rtExecutable = settings.rtExecutable;
//        rulesPath = settings.builtinRulesPath;
        nodeInterpreter = settings.nodeInterpreter;
        treatAsWarnings = settings.treatAllIssuesAsWarnings;
        pluginEnabled = settings.pluginEnabled;

//        RuleCache.initializeFromPath(project, this);
        RTFileListener.start(project);

        settingValidStatus = true;
        return true;
    }

    private boolean validateField(String fieldName, String value, boolean shouldBeAbsolute, boolean allowEmpty, boolean isFile) {
//        ValidationStatus r = FileUtils.validateProjectPath(shouldBeAbsolute ? null : project, value, allowEmpty, isFile);
//        if (isFile) {
//            if (r == ValidationStatus.NOT_A_FILE) {
//                String msg = RTBundle.message("eslint.file.is.not.a.file", fieldName, value);
//                validationFailed(msg);
//                return false;
//            }
//        } else {
//            if (r == ValidationStatus.NOT_A_DIRECTORY) {
//                String msg = RTBundle.message("eslint.directory.is.not.a.dir", fieldName, value);
//                validationFailed(msg);
//                return false;
//            }
//        }
//        if (r == ValidationStatus.DOES_NOT_EXIST) {
//            String msg = RTBundle.message("eslint.file.does.not.exist", fieldName, value);
//            validationFailed(msg);
//            return false;
//        }
        return true;
    }

    private void validationFailed(String msg) {
        NotificationListener notificationListener = new NotificationListener() {
            @Override
            public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event) {
//                ESLintInspection.showSettings(project);
            }
        };
        String errorMessage = msg + FIX_CONFIG_HREF;
        showInfoNotification(errorMessage, NotificationType.WARNING, notificationListener);
        LOG.debug(msg);
        settingValidStatus = false;
    }

    protected void showErrorConfigNotification(String content) {
        if (!settings.getVersion().equals(settingVersionLastShowNotification)) {
            settingVersionLastShowNotification = settings.getVersion();
            showInfoNotification(content, NotificationType.WARNING);
        }
    }

    public void showInfoNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public void showInfoNotification(String content, NotificationType type, NotificationListener notificationListener) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type, notificationListener);
        Notifications.Bus.notify(errorNotification, this.project);
    }

    public static void showNotification(String content, NotificationType type) {
        Notification errorNotification = new Notification(PLUGIN_NAME, PLUGIN_NAME, content, type);
        Notifications.Bus.notify(errorNotification);
    }
}
