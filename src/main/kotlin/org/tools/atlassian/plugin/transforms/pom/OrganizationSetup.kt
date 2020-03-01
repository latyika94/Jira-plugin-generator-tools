package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.escapeXml
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import java.nio.file.Path

object OrganizationSetup : IPomXmlTransformation {
    private val organizationNameDef = ApplicationProperty.getString("pom.organization.name", "Example Company")
    private val organizationUrlDef = ApplicationProperty.getString("pom.organization.url", "http://www.example.com")

    private fun setOrganization(pomXmlBuilder: StringBuilder, orgName: String, orgUrl: String) {
        val template = """
            <organization>    
                <name><![CDATA[${orgName.escapeXml()}]]></name> 
                <url><![CDATA[${orgUrl.escapeXml()}]]></url>
            </organization>
        """.trimIndent()

        pomXmlBuilder.replace(0, pomXmlBuilder.length, pomXmlBuilder.replace("<organization>(.+?)</organization>".toRegex(RegexOption.DOT_MATCHES_ALL), template))

        printProcess("pom.xml: Changing <organization> name and url has been done!")
    }

    override fun execute(pluginPath: Path, pluginData: PluginData, pomXmlBuilder: StringBuilder) {
        val orgName = readStringWithQuestion("Organization name($organizationNameDef)", organizationNameDef)
        val orgUrl = readStringWithQuestion("Organization url($organizationUrlDef)", organizationUrlDef)

        setOrganization(pomXmlBuilder, orgName, orgUrl)
    }
}