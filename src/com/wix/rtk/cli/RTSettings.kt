package com.wix.rtk.cli

import com.wix.rt.settings.Settings

/**
 * RT settings
 * Created by idok on 11/17/14.
 */
data class RTSettings(val node: String, val rtExe: String, val cwd: String, val config: String = "", val targetFile: String = "",
                      val targetVersion: String = "", val modules: String = "", val dryRun: Boolean = false, val reactNative: Boolean = false) {
    companion object {
        @JvmStatic
        fun build(cwd: String, nodeInterpreter: String, rtBin: String, path: String = "", modules: String = "", dryRun: Boolean = false, reactNative: Boolean = false): RTSettings =
                RTSettings(nodeInterpreter, rtBin, "", cwd, path, "", modules, dryRun, reactNative)

        @JvmStatic
        fun buildVersion(cwd: String, nodeInterpreter: String, rtBin: String): RTSettings = RTSettings(nodeInterpreter, rtBin, cwd)

        @JvmStatic
        fun buildSettings(settings: Settings, cwd: String, path: String): RTSettings =
                RTSettings(settings.nodeInterpreter, settings.rtExecutable, cwd, "", path, settings.targetVersion, settings.modules, false, settings.reactNative)
    }
}

data class NodeExe(val node: String, val exe: String, val cwd: String? = null)

//class RTVersion(node: String, exe: String, cwd: String? = null, val x:String) : NodeExe(node, exe, cwd)
