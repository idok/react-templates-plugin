package com.wix.rt.cli;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.wix.nodejs.NodeRunner;
import org.jetbrains.annotations.NotNull;

/**
 * build cli command for RT
 * Created by idok on 11/27/14.
 */
final class RTCliBuilder {

    public static final String MODULES = "--modules";
    public static final String DRY_RUN = "--dry-run";
    public static final String FORCE = "--force";
    public static final String FORMAT = "--format";
    public static final String JSON = "json";
    public static final String LIST_TARGET_VERSION = "--list-target-version";
    public static final String V = "-v";

    private RTCliBuilder() {
    }

    @NotNull
    static GeneralCommandLine listVersions(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter(LIST_TARGET_VERSION);
        commandLine.addParameter(FORMAT);
        commandLine.addParameter(JSON);
        return commandLine;
    }

    @NotNull
    static GeneralCommandLine version(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter(V);
        return commandLine;
    }

    @NotNull
    static GeneralCommandLine createCommandLine(@NotNull RTSettings settings) {
        return NodeRunner.createCommandLine(settings.cwd, settings.node, settings.rtExecutablePath);
    }

    @NotNull
    static GeneralCommandLine createCommandLineBuild(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
//        if (!new File(settings.cwd, settings.targetFile).exists()) {
//            throw new IllegalArgumentException("targetFile doesn't exist");
//        }
        commandLine.addParameter(settings.targetFile);
        commandLine.addParameter(MODULES);
        commandLine.addParameter(settings.modules);
        if (settings.dryRun) {
            commandLine.addParameter(DRY_RUN);
        }
        commandLine.addParameter(FORMAT);
        commandLine.addParameter(JSON);
        return commandLine;
    }

    @NotNull
    static GeneralCommandLine createCommandLineValidate(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
//        if (!new File(settings.cwd, settings.targetFile).exists()) {
//            throw new IllegalArgumentException("targetFile doesn't exist");
//        }
        commandLine.addParameter(settings.targetFile);
        commandLine.addParameter(MODULES);
        commandLine.addParameter(settings.modules);
        commandLine.addParameter(DRY_RUN); //no generation
        commandLine.addParameter(FORCE); //validate with force
        commandLine.addParameter(FORMAT);
        commandLine.addParameter(JSON);
        return commandLine;
    }
}
