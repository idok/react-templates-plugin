package com.wix.rt.cli;

import com.wix.rt.settings.Settings;
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
    public boolean reactNative;

    public static RTSettings build(@NotNull String cwd, @NotNull String nodeInterpreter, @NotNull String rtBin, @NotNull String path, @NotNull String modules, boolean dryRun, boolean reactNative) {
        RTSettings settings = new RTSettings();
        settings.cwd = cwd;
        settings.rtExecutablePath = rtBin;
        settings.node = nodeInterpreter;
        settings.targetFile = path;
        settings.modules = modules;
        settings.dryRun = dryRun;
        settings.reactNative = reactNative;
        return settings;
    }

    public static RTSettings build(Settings settings) {
        RTSettings s = new RTSettings();
        s.rtExecutablePath = settings.rtExecutable;
        s.node = settings.nodeInterpreter;
        s.modules = settings.modules;
        s.reactNative = settings.reactNative;
        s.targetVersion = settings.targetVersion;
        return s;
    }

    public static RTSettings build(@NotNull Settings settings, @NotNull String cwd, @NotNull String path) {
        RTSettings s = build(settings);
        s.cwd = cwd;
        s.targetFile = path;
        return s;
    }

    public static RTSettings build(@NotNull String cwd, @NotNull String nodeInterpreter, @NotNull String rtBin) {
        return build(cwd, nodeInterpreter, rtBin, "", "", false, false);
    }
}
