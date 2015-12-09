package com.wix.rtk.cli

/**
 * RT settings
 * Created by idok on 11/17/14.
 */
data class RTSettingsK(val node: String, val rtExe: String, val cwd: String, val config: String = "", val targetFile: String = "",
                       val targetVersion: String = "", val modules: String = "", val dryRun: Boolean = false, val reactNative: Boolean = false) {
    companion object {
        fun build(cwd: String, nodeInterpreter: String, rtBin: String, path: String = "", modules: String = "", dryRun: Boolean = false, reactNative: Boolean = false): RTSettingsK =
                RTSettingsK(nodeInterpreter, rtBin, "", cwd, path, "", modules, dryRun, reactNative)
    }
}

data class NodeExe(val node: String, val exe: String, val cwd: String? = null)

//class RTVersion(node: String, exe: String, cwd: String? = null, val x:String) : NodeExe(node, exe, cwd)
