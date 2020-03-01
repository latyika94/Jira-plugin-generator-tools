package org.tools.atlassian.plugin.transforms

import net.revelc.code.formatter.xml.lib.FormattingPreferences
import net.revelc.code.formatter.xml.lib.XmlDocumentFormatter
import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.transforms.pom.*
import java.nio.file.Files
import java.nio.file.Path

object PomXmlTransformation : AbstractPluginTransform() {

    override fun execute(pluginPath: Path, pluginData: PluginData) {
        val pomXmlPath = pluginPath.resolve("pom.xml")
        val pomXmlOldPath = pluginPath.resolve("pom-old.xml")
        val pomString = StringBuilder(String(Files.readAllBytes(pomXmlPath)))

        listOf(
                JiraApplicationSetup,
                OrganizationSetup,
                MavenCompilerVersion,
                MavenDistributionManagement,
                ChangeSpringScannerVersion,
                KotlinSupport
        ).forEach {
            it.execute(pluginPath, pluginData, pomString)
        }

        Files.copy(pomXmlPath, pomXmlOldPath)
        printProcess("Backup pom.xml has been done! Backup file: $pomXmlOldPath")

        pomXmlPath.toFile().writeBytes(XmlDocumentFormatter(System.lineSeparator(), FormattingPreferences()).format(pomString.toString()).toByteArray())

        printProcess("Overwrite existing pom.xml has been done!")
    }
}