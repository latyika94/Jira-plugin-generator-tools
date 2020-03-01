package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import java.nio.file.Path

interface IPomXmlTransformation {
    fun execute(
            pluginPath: Path,
            pluginData: PluginData,
            pomXmlBuilder: StringBuilder)
}