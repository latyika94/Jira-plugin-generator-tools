package org.tools.atlassian.plugin.xml.atlassian

import java.nio.file.Path
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlValue

/**
 * Example
 */
/*
<atlassian-plugin key="${atlassian.plugin.key}" name="${project.name}" plugins-version="2">
    <plugin-info>
        <description>${project.description}</description>
        <version>${project.version}</version>
        <vendor name="${project.organization.name}" url="${project.organization.url}" />
        <param name="plugin-icon">images/pluginIcon.png</param>
        <param name="plugin-logo">images/pluginLogo.png</param>
    </plugin-info>

    <!-- add our i18n resource -->
    <resource type="i18n" name="i18n" location="jira-plugin-test"/>

    <!-- add our web resources -->
    <web-resource key="jira-plugin-test-resources" name="jira-plugin-test Web Resources">
        <dependency>com.atlassian.auiplugin:ajs</dependency>

        <resource type="download" name="jira-plugin-test.css" location="/css/jira-plugin-test.css"/>
        <resource type="download" name="jira-plugin-test.js" location="/js/jira-plugin-test.js"/>
        <resource type="download" name="images/" location="/images"/>

        <context>jira-plugin-test</context>
    </web-resource>

</atlassian-plugin>
 */

@XmlRootElement(name = "atlassian-plugin")
data class AtlassianPlugin(

        @get:XmlAttribute
        var key: String = "",

        @get:XmlAttribute
        var name: String = "",

        @get:XmlAttribute(name = "plugins-version")
        var pluginsVersion: String = "",

        @get:XmlElement(name = "plugin-info")
        var pluginInfo: PluginInfo = PluginInfo(),

        @get:XmlElement(name = "resource")
        var resources: MutableList<Resource> = mutableListOf(),

        @get:XmlElement(name = "web-resource")
        var webResources: MutableList<WebResource> = mutableListOf()
) {
    fun write(path: Path) {
        JAXBContext.newInstance(AtlassianPlugin::class.java).createMarshaller().let { marshaller ->
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)

            //Print XML String to Console
            marshaller.marshal(this, path.toFile())
        }
    }

    companion object {
        fun read(path: Path): AtlassianPlugin = path.toFile().inputStream().use { inputStream ->
            JAXBContext.newInstance(AtlassianPlugin::class.java).createUnmarshaller().unmarshal(inputStream) as AtlassianPlugin
        }
    }
}

data class PluginInfo(
        @get:XmlElement
        var description: String = "",

        @get:XmlElement
        var version: String = "",

        @get:XmlElement
        var vendor: Vendor = Vendor(),

        @get:XmlElement(name = "param")
        var params: MutableList<Param> = mutableListOf()
)

data class Vendor(
        @get:XmlAttribute
        var url: String = "",

        @get:XmlAttribute
        var name: String = ""
)

data class Param(
        @get:XmlAttribute
        var name: String = "",

        @get:XmlValue
        var value: String = ""
)

data class Resource(
        @get:XmlAttribute
        var type: String = "",

        @get:XmlAttribute
        var name: String = "",

        @get:XmlAttribute
        var location: String = ""
)


data class WebResource(
        @get:XmlAttribute
        var key: String = "",

        @get:XmlAttribute
        var name: String = "",

        @get:XmlElement(name = "dependency")
        var dependencies: MutableList<String> = mutableListOf(),

        @get:XmlElement(name = "context")
        var contexts: MutableList<String> = mutableListOf(),

        @get:XmlElement(name = "resource")
        var resources: MutableList<Resource> = mutableListOf()
)