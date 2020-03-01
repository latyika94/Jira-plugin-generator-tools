package org.tools.atlassian.plugin.transforms.atlassianxml

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import org.tools.atlassian.plugin.xml.atlassian.AtlassianPlugin
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object PluginIconTransformation : IAtlassianPluginXmlTransformation {

    private val process = ApplicationProperty.getBoolean("pluginIcon.process", false)

    private fun preparePluginIcon(resourcesPath: Path, atlassianPlugin: AtlassianPlugin) {
        if (!readYesOrNoWithQuestion("Do you want to change plugin icon? (Default: ${process.toYesOrNo()})", process))
            return

        val pluginIconPathStr = readStringWithQuestion("Absolute path of you plugin icon: ", ApplicationProperty.getString("pluginIcon.absolutePath", ""))
        check(pluginIconPathStr.isNotBlank()) {
            "Path of your plugin icon is empty!"
        }

        val newPluginIconPath = Paths.get(pluginIconPathStr)

        val pluginIcon = atlassianPlugin.pluginInfo.params.first { it.name == "plugin-icon" }.value
        val actIconPath = resourcesPath.resolve(pluginIcon)

        Files.copy(newPluginIconPath, actIconPath, StandardCopyOption.REPLACE_EXISTING)

        printProcess("Copy plugin icon from [$newPluginIconPath] to [$actIconPath] has been done!")
    }

    override fun execute(pluginPath: Path,
                         pluginData: PluginData,
                         atlassianPlugin: AtlassianPlugin) {
        val resourcesPath = pluginPath.resolve("src").resolve("main").resolve("resources")

        preparePluginIcon(resourcesPath, atlassianPlugin)
    }
}