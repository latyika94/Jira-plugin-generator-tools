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

object PluginLogoTransformation : IAtlassianPluginXmlTransformation {

    private val process = ApplicationProperty.getBoolean("pluginLogo.process", false)

    private fun preparePluginLogo(resourcesPath: Path, atlassianPlugin: AtlassianPlugin) {
        if (!readYesOrNoWithQuestion("Do you want to change plugin logo? (Default: ${process.toYesOrNo()})", process))
            return

        val pluginLogoPathStr = readStringWithQuestion("Absolute path of you plugin logo: ", ApplicationProperty.getString("pluginLogo.absolutePath", ""))
        check(pluginLogoPathStr.isNotBlank()) {
            "Path of your plugin logo is empty!"
        }

        val newPluginLogoPath = Paths.get(pluginLogoPathStr)

        val pluginLogo = atlassianPlugin.pluginInfo.params.first { it.name == "plugin-logo" }.value
        val actLogoPath = resourcesPath.resolve(pluginLogo)

        Files.copy(newPluginLogoPath, actLogoPath, StandardCopyOption.REPLACE_EXISTING)

        printProcess("Copy plugin logo from [$newPluginLogoPath] to [$actLogoPath] has been done!")
    }

    override fun execute(pluginPath: Path,
                         pluginData: PluginData,
                         atlassianPlugin: AtlassianPlugin) {
        val resourcesPath = pluginPath.resolve("src").resolve("main").resolve("resources")

        preparePluginLogo(resourcesPath, atlassianPlugin)
    }
}