package com.wix.rtk.cli

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.util.SystemInfo
import com.wix.nodejs.NodeRunner
import com.wix.rt.build.VerifyMessage
import java.io.File
import java.util.*

/**
 * build cli command for RT
 * Created by idok on 11/27/14.
 */
internal object RTCliBuilder {
    val MODULES = "--modules"
    val DRY_RUN = "--dry-run"
    val FORCE = "--force"
    val FORMAT = "--format"
    val NATIVE = "--native"
    val JSON = "json"
    val LIST_TARGET_VERSION = "--list-target-version"
    val V = "-v"

    fun listVersions(settings: RTSettings): GeneralCommandLine =
            CLI(settings.cwd, settings.node, settings.rtExe)
                    .param(LIST_TARGET_VERSION)
                    .param(FORMAT, JSON)
                    .command

    fun version(settings: RTSettings): GeneralCommandLine = CLI(settings.cwd, settings.node, settings.rtExe).param(V).command

    fun createCommandLineBuild(settings: RTSettings): GeneralCommandLine =
            CLI(settings.cwd, settings.node, settings.rtExe)
                    .param(settings.targetFile)
                    .param(MODULES, settings.modules)
                    .flag(settings.dryRun, DRY_RUN)
                    .flag(settings.reactNative, NATIVE)
                    .param(FORMAT, JSON)
                    .command

    fun createCommandLineValidate(settings: RTSettings): GeneralCommandLine =
            CLI(settings.cwd, settings.node, settings.rtExe)
                    .param(settings.targetFile)
                    .param(MODULES, settings.modules)
                    .param(DRY_RUN) //no generation
                    .param(FORCE) //validate with force
                    .param(FORMAT, JSON)
                    .flag(settings.reactNative, NATIVE)
                    .command
}

class CLI(val cwd: String, val node: String, val exePath: String) {
    val command = NodeRunner.createCommandLine(cwd, node, exePath)

    fun param(name: String): CLI {
        command.addParameter(name)
        return this
    }

    fun param(name: String, value: String): CLI {
        command.addParameter(name)
        command.addParameter(value)
        return this
    }

    fun flag(value: Boolean, flagName: String): CLI {
        if (value) {
            command.addParameter(flagName)
        }
        return this
    }
}

class CLI2(val cwd: String, val exe: String) {
    val command = createCommandLine()

    fun param(name: String): CLI2 {
        command.addParameter(name)
        return this
    }

    fun param(name: String, value: String): CLI2 {
        command.addParameter(name)
        command.addParameter(value)
        return this
    }

    fun flag(value: Boolean, flagName: String): CLI2 {
        if (value) {
            command.addParameter(flagName)
        }
        return this
    }

    fun createCommandLine(): GeneralCommandLine {
        val commandLine = GeneralCommandLine()
        if (!File(cwd).exists() || !File(exe).exists()) {
            throw IllegalArgumentException("path doesn't exist")
        }
        commandLine.setWorkDirectory(cwd)
        //        if (SystemInfo.isWindows) {
        commandLine.exePath = exe
        //        } else {
        //            if (!File(node).exists()) {
        //                throw IllegalArgumentException("path doesn't exist")
        //            }
        //            commandLine.setExePath(node)
        //        }
        return commandLine
    }
}

/**
 * RT result
 * Created by idok on 8/25/14.
 */
class Result(val warns: List<VerifyMessage>, val errorOutput: String = "")

//class VerifyMessage(var level: String = "", var msg: String = "", var file: String = "", var line: Int = 0, var column: Int = 0,
//                    var index: Int = 0, var startOffset: Int = 0, var endOffset: Int = 0) {
//    companion object {
//        val WARN = "WARN"
//        val ERROR = "ERROR"
//        val INFO = "INFO"
//    }
//}
