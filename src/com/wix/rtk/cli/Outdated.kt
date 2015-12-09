package com.wix.rtk.cli

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

class Outdated(@SerializedName("react-templates") val rt: OutdatedClass = OutdatedClass()) {
    companion object {
        fun parseNpmOutdated(json: String): Outdated {
            val builder = GsonBuilder()
            //        builder.registerTypeAdapterFactory(adapter);
            val g = builder.setPrettyPrinting().create()
            val listType = object : TypeToken<Outdated>() {}.type
            return g.fromJson<Outdated>(json, listType)
        }
    }
}

class OutdatedClass(val current: String = "", val wanted: String = "", val latest: String = "", val location: String = "")