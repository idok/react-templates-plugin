package com.wix.rt.cli;

import com.intellij.openapi.util.SystemInfo;
import com.wix.nodejs.NodeFinder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class RTFinder {
    public static final String RT_BASE_NAME = SystemInfo.isWindows ? "rt.cmd" : "rt";

    private RTFinder() {
    }

    @NotNull
    public static List<File> searchForRTBin(File projectRoot) {
        return NodeFinder.searchAllScopesForBin(projectRoot, RT_BASE_NAME);
    }
}