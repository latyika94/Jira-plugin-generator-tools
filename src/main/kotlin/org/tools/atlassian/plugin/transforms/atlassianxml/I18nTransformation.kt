package org.tools.atlassian.plugin.transforms.atlassianxml

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import org.tools.atlassian.plugin.xml.atlassian.AtlassianPlugin
import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.Path

object I18nTransformation : IAtlassianPluginXmlTransformation {
    private val process = ApplicationProperty.getBoolean("i18nTransform.process", false)

    private fun rebaseI18nProperty(resourcesPath: Path, atlassianPlugin: AtlassianPlugin, pluginData: PluginData) {
        val propFileName = "${pluginData.artifactId}.properties"
        if (!readYesOrNoWithQuestion("Do you want to move [$propFileName] file to [i18n] directory? (Default: ${process.toYesOrNo()})", process))
            return

        if(!resourcesPath.resolve("i18n").toFile().mkdirs()) {
            throw IllegalStateException("Could not create i18n directory in resources folder!")
        }

        Files.move(resourcesPath.resolve(propFileName), resourcesPath.resolve("i18n").resolve(propFileName))
        printProcess("Copy $propFileName to i18n/$propFileName has been done!")

        val i18n = atlassianPlugin.resources.first { it.type == "i18n" }
        i18n.location = "i18n.${pluginData.artifactId}"

        printProcess("""Modify atlassian.plugin.xml <resource type="i18n" ... /> location has been done!""")
    }

    override fun execute(pluginPath: Path,
                         pluginData: PluginData,
                         atlassianPlugin: AtlassianPlugin) {
        val resourcesPath = pluginPath.resolve("src").resolve("main").resolve("resources")

        rebaseI18nProperty(resourcesPath, atlassianPlugin, pluginData)
    }
}