package com.wix.rt.build;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.wix.rt.RTProjectComponent;
import com.wix.rt.settings.Settings;
import com.wix.rt.utils.RTRunner;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RTFileListener {
    private final Project project;
    private final AtomicBoolean LISTENING = new AtomicBoolean(false);
    private RTFileVfsListener listener;

    public RTFileListener(@NotNull Project project) {
        this.project = project;
    }

    private void startListener() {
        if (LISTENING.compareAndSet(false, true))
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            listener = new RTFileVfsListener();
                            VirtualFileManager.getInstance().addVirtualFileListener(listener, RTFileListener.this.project);
//                            EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
//                            multicaster.addDocumentListener(new RTFileDocumentListener(), RTFileListener.this.project);
                        }
                    });
                }
            });
    }

    private void stopListener() {
        if (LISTENING.compareAndSet(true, false))
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    ApplicationManager.getApplication().runWriteAction(new Runnable() {
                        public void run() {
                            VirtualFileManager.getInstance().removeVirtualFileListener(listener);
//                            EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
//                            multicaster.addDocumentListener(new RTFileDocumentListener(), RTFileListener.this.project);
                        }
                    });
                }
            });
    }

    public static void start(@NotNull Project project) {
        RTFileListener listener = ServiceManager.getService(project, RTFileListener.class);
        listener.startListener();
    }

    public static void stop(@NotNull Project project) {
        RTFileListener listener = ServiceManager.getService(project, RTFileListener.class);
        listener.stopListener();
    }

    private void fileChanged(@NotNull VirtualFile file) {
        if (RTFileUtil.isRTFile(file) && !project.isDisposed()) {
//            restartAnalyzer();
            compile(file);
        }
    }

    private void compile(@NotNull final VirtualFile file) {
        compile(file, project);
    }

    public static void compile(@NotNull final VirtualFile file, @NotNull final Project project) {
        RTProjectComponent component = project.getComponent(RTProjectComponent.class);
        if (!component.isEnabled()) {
           return;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Settings settings = Settings.getInstance(project);
                    Result out = RTRunner.build(project.getBasePath(), file.getPath(), settings.nodeInterpreter, settings.rtExecutable, settings.modules);
                } catch (Exception e1) {
                    e1.printStackTrace();
//                    warn(project, e1.toString());
                }
            }
        };

//        ApplicationManager.getApplication().runWriteAction()



        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
//                final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
                Task.Backgroundable task = new Task.Backgroundable(project, "Compile RT", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        try {
                            Settings settings = Settings.getInstance(project);
                            Result out = RTRunner.build(project.getBasePath(), file.getPath(), settings.nodeInterpreter, settings.rtExecutable, settings.modules);
                            file.getParent().refresh(false, false);
                        } catch (Exception e1) {
                            e1.printStackTrace();
//                    warn(project, e1.toString());
                        }
                    }
                };

                final ProgressIndicator indicator = new BackgroundableProcessIndicator(task);
                ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, indicator);
            }
        });
//        ApplicationManager.getApplication().runWriteAction();

//        if (ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, "compile rt", true, project)) {
//            file.getParent().refresh(false, false);
//
////            final DartListPackageDirsDialog dialog = new DartListPackageDirsDialog(project, packageNameToDirMap);
////            dialog.show();
////
////            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
////                configurePubListPackageDirsLibrary(project, affectedModules, packageNameToDirMap);
////            }
////
////            if (dialog.getExitCode() == DartListPackageDirsDialog.CONFIGURE_NONE_EXIT_CODE) {
////                removePubListPackageDirsLibrary(project);
////            }
//        }
    }

    private void restartAnalyzer() {
        RTProjectComponent component = project.getComponent(RTProjectComponent.class);
        if (component.isEnabled()) {
            DaemonCodeAnalyzer.getInstance(project).restart();
        }
    }

    /**
     * VFS Listener
     */
    private class RTFileVfsListener extends VirtualFileAdapter {
        private RTFileVfsListener() {
        }

        public static final String NAME = "name";

        public void fileCreated(@NotNull VirtualFileEvent event) {
//            System.out.println("fileCreated " + event.getFileName());
            RTFileListener.this.fileChanged(event.getFile());
        }

        public void fileDeleted(@NotNull VirtualFileEvent event) {
//            System.out.println("fileDeleted " + event.getFileName());
            RTFileListener.this.fileChanged(event.getFile());
//            VirtualFile file = event.getFile();
//            if (RTFileUtil.isRTFile(file)) { //and file is rt with rt.js
//                VirtualFile jsFile = file.getParent().findChild(RTFileUtil.getJsRTFileName(file));
//                if (jsFile != null) {
//                        VfsLocationChangeDialog d = new VfsLocationChangeDialog(new LessProjectState());
//                        d.shouldDeleteCssFile(event);
////                        jsFile.rename(this, RTFileUtil.getJsRTFileName((String) event.getNewValue()));
//                }
//            }
        }

        public void fileMoved(@NotNull VirtualFileMoveEvent event) {
//            RTProjectComponent component = project.getComponent(RTProjectComponent.class);
//            component.vfsLocationChangeDialog.s
//            event.
//            System.out.println("fileMoved " + event.getFileName());
            RTFileListener.this.fileChanged(event.getFile());
        }

        public void fileCopied(@NotNull VirtualFileCopyEvent event) {
//            System.out.println("fileCopied " + event.getFileName());
            RTFileListener.this.fileChanged(event.getFile());
            RTFileListener.this.fileChanged(event.getOriginalFile());
        }

        @Override
        public void contentsChanged(@NotNull VirtualFileEvent event) {
//            System.out.println("contentsChanged " + event.getFileName());
            RTFileListener.this.fileChanged(event.getFile());
        }

        @Override
        public void propertyChanged(@NotNull VirtualFilePropertyEvent event){
//            System.out.println("propertyChanged " + event.getFileName());
            VirtualFile file = event.getFile();
            if (event.getPropertyName().equals(NAME) && RTFileUtil.isRTFile(file)) { //and file is rt with rt.js
                VirtualFile jsFile = file.getParent().findChild(RTFileUtil.getJsRTFileName((String) event.getOldValue()));
                if (jsFile != null) {
                    try {
                        jsFile.rename(this, RTFileUtil.getJsRTFileName((String) event.getNewValue()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Document Listener
     */
    private class RTFileDocumentListener extends DocumentAdapter {
        private RTFileDocumentListener() {
        }

        public void documentChanged(DocumentEvent event) {
            VirtualFile file = FileDocumentManager.getInstance().getFile(event.getDocument());
            if (file != null) {
                RTFileListener.this.fileChanged(file);
            }
        }
    }
}

