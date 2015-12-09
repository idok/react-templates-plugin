package com.wix.rtk.cli

//import com.wix.rt.cli.Outdated
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

    val FORCE = "--force"
    val AMD = "amd"
    val COMMONJS = "commonjs"
    val NONE = "none"
    val ES6 = "es6"
    val TYPESCRIPT = "typescript"

    private val LOG = Logger.getInstance(RTRunner::class.java)

    public val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()

    fun convertFile(settings: RTSettingsK): ProcessOutput {
        val commandLine = RTCliBuilderK.createCommandLineBuild(settings)
        return NodeRunner.execute(commandLine, TIME_OUT)
    }

    fun build(settings: RTSettingsK): ResultK {
        val warns = try {
            val output = RTRunner.convertFile(settings)
            parse(output.stdout)
        } catch (e: ExecutionException) {
            handleError("Error running React-Templates build: ${e.message}\ncwd: ${settings.cwd}\ncommand: ${settings.rtExe}", e)
            emptyList<VerifyMessage>()
        }
        return ResultK(warns)
    }

    //rt --list-target-version -f json
    fun listVersion(cwd: String, node: String, rtBin: String): ResultK {
        val settings = RTSettingsK.build(cwd, node, rtBin)
        val commandLine = RTCliBuilderK.createCommandLineBuild(settings)
        commandLine.addParameter(FORCE)
        val warns = run(settings, commandLine)
        return ResultK(warns)
    }

    fun listTargetVersions(cwd: String, node: String, rtBin: String): List<String> {
        val settings = RTSettingsK.build(cwd, node, rtBin)
        return listTargetVersions(settings)
    }

    fun listTargetVersions(settings: RTSettingsK): List<String> = try {
        val commandLine = RTCliBuilderK.listVersions(settings)
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
        return g.fromJson<List<String>>(json, listType)
    }

    fun compile(settings: RTSettingsK): ResultK {
        val settings2 = settings.copy(dryRun = true)
        val commandLine = RTCliBuilderK.createCommandLineValidate(settings2)
        val warns = run(settings, commandLine)
        return ResultK(warns)
    }

    fun run(settings: RTSettingsK, commandLine: GeneralCommandLine): List<VerifyMessage> = try {
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

    private fun version(settings: RTSettingsK): ProcessOutput {
        val commandLine = RTCliBuilderK.version(settings)
        return NodeRunner.execute(commandLine, TIME_OUT)
    }

    fun runVersion(settings: RTSettingsK): String {
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