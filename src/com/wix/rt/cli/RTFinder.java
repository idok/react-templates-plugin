package com.wix.rt.cli;

import com.wix.nodejs.NodeFinder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class RTFinder {
    public static final String RT_BASE_NAME = NodeFinder.getBinName("rt");

    private RTFinder() {
    }

    @NotNull
    public static List<File> searchForRTBin(File projectRoot) {
        return NodeFinder.searchAllScopesForBin(projectRoot, RT_BASE_NAME);
    }
}