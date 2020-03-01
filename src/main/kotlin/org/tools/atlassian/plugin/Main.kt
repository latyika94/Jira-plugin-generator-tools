package org.tools.atlassian.plugin

import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.transforms.AbstractPluginTransform
import org.tools.atlassian.plugin.transforms.AtlassianPluginXmlTransformation
import org.tools.atlassian.plugin.transforms.PomXmlTransformation
import java.nio.file.Paths

fun main() {
    val defaultGroupId = ApplicationProperty.getString("plugin.groupId", "com.atlassian.plugin.test")
    val defaultArtifactId = ApplicationProperty.getString("plugin.artifactId", "jira-plugin-test")
    val defaultVersion = ApplicationProperty.getString("plugin.version", "1.0.0-SNAPSHOT")
    val defaultPackagePath = ApplicationProperty.getString("plugin.packagePath", "com.atlassian.plugin.test")

    val groupId: String = readStringWithQuestion("Jira plugin groupId ($defaultGroupId)", defaultGroupId)
    val artifactId: String = readStringWithQuestion("Jira plugin artifactId ($defaultArtifactId)", defaultArtifactId)
    val version: String = readStringWithQuestion("Jira plugin version ($defaultVersion)", defaultVersion)
    val packagePath: String = readStringWithQuestion("Jira plugin package ($defaultPackagePath)", defaultPackagePath)

    val pluginData = PluginData(
            groupId = groupId,
            artifactId = artifactId,
            version = version,
            packagePath = packagePath
    )

    val atlassianCreateJiraPluginCmd = "cmd /c atlas-create-jira-plugin --group-id $groupId --artifact-id $artifactId --version $version --package $packagePath --non-interactive"
    printProcess("Running process: $atlassianCreateJiraPluginCmd")
    printProcess("Waiting for process result")
    val process: Process = Runtime.getRuntime().exec(atlassianCreateJiraPluginCmd)
    process.inputStream.reader(Charsets.UTF_8).use {
        println(it.readText())
    }
    printProcess("atlas-create-jira-plugin process has been finished. Base jira addon skeleton has been created.")

    val currentPath = Paths.get(".").resolve(artifactId)

    listOf(
            PomXmlTransformation,
            AtlassianPluginXmlTransformation
    ).forEach {
        it.execute(currentPath, pluginData)
    }

}