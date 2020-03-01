package org.tools.atlassian.plugin.config

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

object ApplicationProperty {
    private val property: Properties by lazy {
        Properties().also { prop ->
            val propName = "application.properties"
            val localConfig = Paths.get(".").resolve(propName)

            if (Files.exists(localConfig)) {
                Files.newInputStream(localConfig).use {
                    prop.load(it)
                }
            } else {
                javaClass.getResourceAsStream(propName).use {
                    prop.load(it)
                }
            }
        }
    }

    fun getBoolean(key: String, default: Boolean) : Boolean = property.getProperty(key, default.toString()) == "true"
    fun getString(key: String, default: String) : String = property.getProperty(key, default)
}