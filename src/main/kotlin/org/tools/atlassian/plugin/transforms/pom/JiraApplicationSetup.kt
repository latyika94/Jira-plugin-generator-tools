package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import java.nio.file.Path

object JiraApplicationSetup : IPomXmlTransformation {
    private val jiraDefVersion = ApplicationProperty.getString("jira.version.software", "8.6.1")
    private val jiraServiceDeskDefVersion = ApplicationProperty.getString("jira.version.servicedesk", "4.6.1")

    private val process = ApplicationProperty.getBoolean("jira.process.servicedesk", false)

    private fun setJiraVersion(pomXmlBuilder: StringBuilder) {
        val jiraVersion = readStringWithQuestion("Jira application version ($jiraDefVersion)", jiraDefVersion)
        pomXmlBuilder.replace(0, pomXmlBuilder.length, pomXmlBuilder.replace("<jira.version>(.+?)</jira.version>".toRegex(RegexOption.DOT_MATCHES_ALL), "<jira.version>$jiraVersion</jira.version>"))

        printProcess("pom.xml: Setting <jira.version> in <properties> has been done!")
    }

    private fun setJiraServiceDeskProperty(pomXmlBuilder: StringBuilder, serviceDeskVersion: String) {
        val template = "<jira.servicedesk.application.version>$serviceDeskVersion</jira.servicedesk.application.version>"

        pomXmlBuilder.insert(pomXmlBuilder.indexOf("</properties>"), template)

        printProcess("pom.xml: Setting <jira.servicedesk.application.version> in <properties> has been done!")
    }

    private fun setJiraServiceDeskApplication(pomXmlBuilder: StringBuilder) {
        val template = """
            <products>
                <product>
                    <id>jira</id>
                    <instanceId>jira</instanceId>
                    <version>${"\\$"}{jira.version}</version>
                    <applications>
                        <application>
                            <applicationKey>jira-servicedesk</applicationKey>
                            <version>${"\\$"}{jira.servicedesk.application.version}</version>
                        </application>
                    </applications>
                    <pluginArtifacts>
                     
                    </pluginArtifacts>
                </product>
            </products>
        """.trimIndent()
        pomXmlBuilder.replace(0,
                pomXmlBuilder.length,
                pomXmlBuilder.replace("""<productVersion>(.+?)</productDataVersion>""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)), template))

        printProcess("pom.xml: Adding jira servicedesk application to jira-maven-plugin <plugin> has been done!")
    }

    private fun fixJiraMavenPluginExport(pomXmlBuilder: StringBuilder, pluginData: PluginData) {
        val template = "<Export-Package>${pluginData.packagePath},</Export-Package>"

        pomXmlBuilder.replace(0,
                pomXmlBuilder.length,
                pomXmlBuilder.replace("""<Export-Package>(.+?)</Export-Package>""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)), template))

        printProcess("pom.xml: Fixing jira-maven-plugin <Export-Package> has been done!")
    }

    private fun addJiraServiceDesk(pomXmlBuilder: StringBuilder) {
        if (!readYesOrNoWithQuestion("Add Jira ServiceDesk application? (Default: ${process.toYesOrNo()})", process))
            return

        val jiraServiceDeskVersion = readStringWithQuestion("Jira ServiceDesk application version ($jiraServiceDeskDefVersion)", jiraServiceDeskDefVersion)
        setJiraServiceDeskProperty(pomXmlBuilder, jiraServiceDeskVersion)
        setJiraServiceDeskApplication(pomXmlBuilder)
    }

    override fun execute(pluginPath: Path, pluginData: PluginData, pomXmlBuilder: StringBuilder) {
        setJiraVersion(pomXmlBuilder)
        addJiraServiceDesk(pomXmlBuilder)
        fixJiraMavenPluginExport(pomXmlBuilder, pluginData)
    }
}