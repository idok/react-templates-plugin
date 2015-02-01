package com.wix.rt.cli;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.wix.nodejs.NodeRunner;
import org.jetbrains.annotations.NotNull;

/**
 * build cli command for RT
 * Created by idok on 11/27/14.
 */
final class RTCliBuilder {

    private RTCliBuilder() {
    }

    @NotNull
    static GeneralCommandLine listVersions(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("--list-target-version");
        commandLine.addParameter("--format");
        commandLine.addParameter("json");
        return commandLine;
    }

    @NotNull
    static GeneralCommandLine version(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
        commandLine.addParameter("-v");
        return commandLine;
    }

    @NotNull
    static GeneralCommandLine createCommandLine(@NotNull RTSettings settings) {
        return NodeRunner.createCommandLine(settings.cwd, settings.node, settings.rtExecutablePath);
    }

    @NotNull
    static GeneralCommandLine createCommandLineLint(@NotNull RTSettings settings) {
        GeneralCommandLine commandLine = createCommandLine(settings);
//        if (!new File(settings.cwd, settings.targetFile).exists()) {
//            throw new IllegalArgumentException("targetFile doesn't exist");
//        }
        commandLine.addParameter(settings.targetFile);
        commandLine.addParameter("--modules");
        commandLine.addParameter(settings.modules);
        if (settings.dryRun) {
            commandLine.addParameter("--dry-run");
        }
        commandLine.addParameter("--format");
        commandLine.addParameter("json");
        return commandLine;
    }
}
