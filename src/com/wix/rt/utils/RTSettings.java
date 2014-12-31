package com.wix.rt.utils;

import org.jetbrains.annotations.NotNull;

/**
 * RT settings
 * Created by idok on 11/17/14.
 */
public class RTSettings {
    public String node;
    public String rtExecutablePath;
    public String config;
    public String cwd;
    public String targetFile;
    public String targetVersion;
    public String modules;
    public boolean dryRun;

    public static RTSettings build(@NotNull String cwd, @NotNull String path, @NotNull String nodeInterpreter, @NotNull String rtBin, @NotNull String modules, boolean dryRun) {
        RTSettings settings = new RTSettings();
        settings.cwd = cwd;
        settings.rtExecutablePath = rtBin;
        settings.node = nodeInterpreter;
        settings.targetFile = path;
        settings.modules = modules;
        settings.dryRun = dryRun;
        return settings;
    }
}
