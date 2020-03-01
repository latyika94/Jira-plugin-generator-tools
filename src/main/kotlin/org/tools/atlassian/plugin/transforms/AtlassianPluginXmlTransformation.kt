package org.tools.atlassian.plugin.transforms

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.transforms.atlassianxml.I18nTransformation
import org.tools.atlassian.plugin.transforms.atlassianxml.PluginIconTransformation
import org.tools.atlassian.plugin.transforms.atlassianxml.PluginLogoTransformation
import org.tools.atlassian.plugin.xml.atlassian.AtlassianPlugin
import java.nio.file.Files
import java.nio.file.Path

object AtlassianPluginXmlTransformation : AbstractPluginTransform() {

    override fun execute(pluginPath: Path, pluginData: PluginData) {

        val resourcePath = pluginPath.resolve("src").resolve("main").resolve("resources")
        val atlassianPluginXmlPath = resourcePath.resolve("atlassian-plugin.xml")
        val atlassianPluginOldXmlPath = resourcePath.resolve("atlassian-plugin-old.xml")

        val atlassianPlugin = AtlassianPlugin.read(atlassianPluginXmlPath)

        listOf(
                PluginIconTransformation,
                PluginLogoTransformation,
                I18nTransformation
        ).forEach {
            it.execute(pluginPath, pluginData, atlassianPlugin)
        }

        Files.copy(atlassianPluginXmlPath, atlassianPluginOldXmlPath)
        printProcess("Backup atlassian-plugin.xml has been done! Backup file: $atlassianPluginOldXmlPath")

        atlassianPlugin.write(atlassianPluginXmlPath)

        printProcess("Overwrite existing atlassin-plugin.xml has been done!")
    }
}