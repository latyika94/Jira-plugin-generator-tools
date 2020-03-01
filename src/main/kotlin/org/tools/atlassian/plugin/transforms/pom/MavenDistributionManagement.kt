package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import java.nio.file.Path

object MavenDistributionManagement : IPomXmlTransformation {

    private val defReleaseId: String = ApplicationProperty.getString("maven.distributionManagement.release.id", "artifactory")
    private val defSnapshotId: String = ApplicationProperty.getString("maven.distributionManagement.snapshot.id", "artifactory")
    private val defReleaseUrl: String = ApplicationProperty.getString("maven.distributionManagement.release.url", "http://maven/releases")
    private val defSnapshotUrl: String = ApplicationProperty.getString("maven.distributionManagement.snapshot.url", "http://maven/snapshots")

    private val process = ApplicationProperty.getBoolean("maven.distributionManagement.process", false)

    private fun setDistributionManagement(
            pomXmlBuilder: StringBuilder,
            releaseId: String,
            releaseUrl: String,
            snapshotId: String,
            snapshotUrl: String
    ) {
        val template = """
           <distributionManagement>
                <repository>
                    <id>$releaseId</id>
                    <url>$releaseUrl</url>
                </repository>
                <snapshotRepository>
                    <id>$snapshotId</id>
                    <url>$snapshotUrl</url>
                </snapshotRepository>
            </distributionManagement>
        """.trimIndent()
        pomXmlBuilder.insert(pomXmlBuilder.indexOf("</properties>") + "</properties>".length, template)


        printProcess("pom.xml: Adding distributionManagement in <project> has been done!")
    }


    override fun execute(pluginPath: Path, pluginData: PluginData, pomXmlBuilder: StringBuilder) {
        if (!readYesOrNoWithQuestion("Do you want to add Maven distributionManagement? (Default: ${process.toYesOrNo()})", process)) return

        val releaseId = readStringWithQuestion("Maven [release] distributionManagement [id]? ($defReleaseId)", defReleaseId)
        val releaseUrl = readStringWithQuestion("Maven [release] distributionManagement [url]? ($defReleaseUrl)", defReleaseUrl)
        val snapshotId = readStringWithQuestion("Maven [snapshot] distributionManagement [id]? ($defSnapshotId)", defSnapshotId)
        val snapshotUrl = readStringWithQuestion("Maven [snapshot] distributionManagement [url]? ($defSnapshotUrl)", defSnapshotUrl)

        setDistributionManagement(pomXmlBuilder, releaseId, releaseUrl, snapshotId, snapshotUrl)
    }
}