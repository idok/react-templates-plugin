package com.wix.rtk.cli

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.intellij.execution.ExecutionException
import com.wix.nodejs.NodeRunner
import com.wix.rtk.cli.RTRunner.TIME_OUT as TIME_OUT

object Npm {
    val REACT_TEMPLATES = "react-templates"
    val OUTDATED = "outdated"
    val VIEW = "view"
    val G = "-g"
    val JSON = "-json"

    // npm view react-templates
    fun view(cwd: String, npm: String): Output {
        return try {
            val command = CLI2(cwd, npm).param(VIEW).param(REACT_TEMPLATES).command
            val output = NodeRunner.execute(command, RTRunner.TIME_OUT)
            val json = output.stdout
            parseNpmOutdated(json)
        } catch (e: ExecutionException) {
            //            RTRunner.LOG.warn("Could not build react-templates file", e)
            e.printStackTrace()
            Output()
        }
    }

    //npm ls react-templates -g --depth 0 --json
    //npm outdated react-templates -g -json
    fun outdated(cwd: String, npm: String): Outdated {
        return try {
            val command = CLI2(cwd, npm).param(OUTDATED).param(REACT_TEMPLATES).param(G).param(JSON).command
            val output = NodeRunner.execute(command, RTRunner.TIME_OUT)
            val json = output.stdout
            Outdated.parseNpmOutdated(json)
        } catch (e: ExecutionException) {
            //            RTRunner.LOG.warn("Could not build react-templates file", e)
            e.printStackTrace()
            Outdated()
        }
    }

    fun parseNpmOutdated(json: String): Output {
        val builder = GsonBuilder()
        val g = builder.setPrettyPrinting().create()
        val listType = object : TypeToken<Output>() {}.type
        return g.fromJson<Output>(json, listType)
    }

    class Output(val name: String = "",
                 @SerializedName("dist-tags") val distTags: Tags = Tags())

    class Tags(val latest: String = "")
}