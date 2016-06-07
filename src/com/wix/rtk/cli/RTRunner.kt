package com.wix.rtk.cli

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.wix.nodejs.NodeRunner
import com.wix.rt.RTProjectComponent
import com.wix.rt.build.VerifyMessage
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object RTRunner {

    const val FORCE = "--force"
    const val AMD = "amd"
    const val COMMONJS = "commonjs"
    const val NONE = "none"
    const val ES6 = "es6"
    const val TYPESCRIPT = "typescript"

    private val LOG = Logger.getInstance(RTRunner::class.java)

    val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()

    fun convertFile(settings: RTSettings): ProcessOutput {
        val commandLine = RTCliBuilder.createCommandLineBuild(settings)
        return NodeRunner.execute(commandLine, TIME_OUT)
    }

    @JvmStatic
    fun build(settings: RTSettings): Result {
        val warns = try {
            val output = RTRunner.convertFile(settings)
            parse(output.stdout)
        } catch (e: ExecutionException) {
            handleError("Error running React-Templates build: ${e.message}\ncwd: ${settings.cwd}\ncommand: ${settings.rtExe}", e)
            emptyList<VerifyMessage>()
        }
        return Result(warns)
    }

    //rt --list-target-version -f json
    fun listVersion(cwd: String, node: String, rtBin: String): Result {
        val settings = RTSettings.build(cwd, node, rtBin)
        val commandLine = RTCliBuilder.createCommandLineBuild(settings)
        commandLine.addParameter(FORCE)
        val warns = run(settings, commandLine)
        return Result(warns)
    }

    @JvmStatic
    fun listTargetVersions(cwd: String, node: String, rtBin: String): List<String> {
        val settings = RTSettings.build(cwd, node, rtBin)
        return listTargetVersions(settings)
    }

    fun listTargetVersions(settings: RTSettings): List<String> = try {
        val commandLine = RTCliBuilder.listVersions(settings)
        val output = NodeRunner.execute(commandLine, TIME_OUT)
        parseVersions(output.stdout)
    } catch (e: ExecutionException) {
        handleError("Error running React-Templates build: ${e.message}\ncwd: ${settings.cwd}\ncommand: ${settings.rtExe}", e)
        emptyList<String>()
    }

    private fun parseVersions(json: String): List<String> {
        val builder = GsonBuilder()
        val g = builder.setPrettyPrinting().create()
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        val list = g.fromJson<List<String>>(json, listType)
        return list ?: emptyList()
    }

    fun compile(settings: RTSettings): Result {
        val settings2 = settings.copy(dryRun = true)
        val commandLine = RTCliBuilder.createCommandLineValidate(settings2)
        val warns = run(settings, commandLine)
        return Result(warns)
    }

    fun run(settings: RTSettings, commandLine: GeneralCommandLine): List<VerifyMessage> = try {
        val output = NodeRunner.execute(commandLine, TIME_OUT)
        parse(output.stdout)
    } catch (e: ExecutionException) {
        handleError("Error running React-Templates build: ${e.message}\ncwd: ${settings.cwd}\ncommand: ${settings.rtExe}", e)
        emptyList<VerifyMessage>()
    }

    private fun handleError(msg: String, e: Exception) {
        LOG.warn(msg, e)
        RTProjectComponent.showNotification(msg, NotificationType.WARNING)
        e.printStackTrace()
    }

    private fun version(settings: RTSettings): ProcessOutput {
        val commandLine = RTCliBuilder.version(settings)
        return NodeRunner.execute(commandLine, TIME_OUT)
    }

    fun runVersion(settings: RTSettings): String {
        if (!File(settings.rtExe).exists()) {
            LOG.warn("Calling version with invalid react-templates exe ${settings.rtExe}")
            return ""
        }
        val out = version(settings)
        return if (out.exitCode == 0) out.stdout.trim { it <= ' ' } else ""
    }

    private fun parse(json: String): List<VerifyMessage> {
        val builder = GsonBuilder()
        // builder.registerTypeAdapterFactory(adapter);
        val g = builder.setPrettyPrinting().create()
        val listType = object : TypeToken<ArrayList<VerifyMessage>>() {}.type
        return g.fromJson<List<VerifyMessage>>(json, listType)
    }
}