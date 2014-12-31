package com.wix.rt.utils;

import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.util.containers.ContainerUtil;
import com.wix.nodejs.NodeFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Set;

public final class RTFinder {
    public static final String RT_BASE_NAME = SystemInfo.isWindows ? "rt.cmd" : "rt";
    public static final String DEFAULT_RT_BIN = SystemInfo.isWindows ? "node_modules\\.bin\\rt.cmd" : "node_modules/react-templates/bin/rt.js";

    // TODO figure out a way to automatically get this path or add it to config
    // should read from /usr/local/lib/node_modules/eslint/lib/rules
//    public static String defaultPath = "/usr/local/lib/node_modules/eslint/lib/rules";
// c:/users/user/appdata/roaming/npm/node_modules

    private RTFinder() {
    }

    // List infos = ContainerUtil.newArrayList();
    // NodeModuleSearchUtil.findModulesWithName(infos, "eslint", project.getBaseDir(), null, false);

    @Nullable
    public static File findInterpreterInPath() {
        return PathEnvironmentVariableUtil.findInPath(RT_BASE_NAME);
    }

    @NotNull
    public static List<File> listPossibleRTExe() {
        Set<File> interpreters = ContainerUtil.newLinkedHashSet();
        List<File> fromPath = PathEnvironmentVariableUtil.findAllExeFilesInPath(RT_BASE_NAME);
        List<File> nvmInterpreters = NodeFinder.listNodeInterpretersFromNvm(RT_BASE_NAME);
        List<File> brewInterpreters = NodeFinder.listNodeInterpretersFromHomeBrew(RT_BASE_NAME);
        interpreters.addAll(fromPath);
        interpreters.removeAll(nvmInterpreters);
        interpreters.removeAll(brewInterpreters);
        interpreters.addAll(nvmInterpreters);
        interpreters.addAll(brewInterpreters);
        return ContainerUtil.newArrayList(interpreters);
    }

    @NotNull
    public static List<File> searchForRTBin(File projectRoot) {
//        List<File> nodeModules = searchProjectNodeModules(projectRoot);
        List<File> globalRTBin = listPossibleRTExe();

        if (SystemInfo.isWindows) {
            File file = NodeFinder.resolvePath(projectRoot, NodeFinder.NODE_MODULES, ".bin", "rt.cmd");
            if (file.exists()) {
                globalRTBin.add(file);
            }
        } else {
            File file = NodeFinder.resolvePath(projectRoot, NodeFinder.NODE_MODULES, "react-templates", "bin", "rt.js");
            if (file.exists()) {
                globalRTBin.add(file);
            }
        }
//        globalRTBin.addAll(nodeModules);
        return globalRTBin;
    }

    public static File guessBestRTBin(File projectPath) {
        List<File> rtBinFiles = RTFinder.searchForRTBin(projectPath);
        if (!rtBinFiles.isEmpty()) {
            return rtBinFiles.get(0);
        }
        return null;
    }
}