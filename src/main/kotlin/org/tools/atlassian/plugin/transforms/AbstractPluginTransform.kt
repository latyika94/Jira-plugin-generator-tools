package org.tools.atlassian.plugin.transforms

import org.tools.atlassian.plugin.PluginData
import java.nio.file.Path

abstract class AbstractPluginTransform {
    abstract fun execute(pluginPath: Path, pluginData: PluginData)
}