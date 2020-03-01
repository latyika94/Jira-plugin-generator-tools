package org.tools.atlassian.plugin.transforms.atlassianxml

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.xml.atlassian.AtlassianPlugin
import java.nio.file.Path

interface IAtlassianPluginXmlTransformation {
    fun execute(
            pluginPath: Path,
            pluginData: PluginData,
            atlassianPlugin: AtlassianPlugin)
}