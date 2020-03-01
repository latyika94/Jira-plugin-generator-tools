package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import java.nio.file.Files
import java.nio.file.Path

object ChangeSpringScannerVersion : IPomXmlTransformation {
    private val defSpringScannerVersion = ApplicationProperty.getString("spring.version.scanner", "2.0.1")
    private val defSpringFrameworkVersion = ApplicationProperty.getString("spring.version.framework", "5.0.10.RELEASE")
    private val process = ApplicationProperty.getBoolean("spring.process", false)

    private fun setAtlassianSpringVersion(pomXmlBuilder: StringBuilder, springScannerVersion:String) {
        pomXmlBuilder.replace(
                0,
                pomXmlBuilder.length,
                pomXmlBuilder.replace("<atlassian.spring.scanner.version>(.+?)</atlassian.spring.scanner.version>".toRegex(RegexOption.DOT_MATCHES_ALL), "<atlassian.spring.scanner.version>$springScannerVersion</atlassian.spring.scanner.version>"))
        printProcess("pom.xml: Setting <atlassian.spring.scanner.version> in <properties> has been done!")
    }

    private fun fixAtlassianSpringScannerDependencies(pomXmlBuilder: StringBuilder) {
        pomXmlBuilder.replace(0,
                pomXmlBuilder.length,
                pomXmlBuilder.replace("""<dependency>\s*<groupId>com.atlassian.plugin<\/groupId>\s*<artifactId>atlassian-spring-scanner-runtime<\/artifactId>\s*<version>\${'$'}\{atlassian.spring.scanner.version}<\/version>\s*<scope>runtime<\/scope>\s*<\/dependency>""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)), ""))

        val template = """
            <dependency>
                <groupId>com.atlassian.plugin</groupId>
                <artifactId>atlassian-spring-scanner-annotation</artifactId>
                <version>${"\\$"}{atlassian.spring.scanner.version}</version>
                <scope>provided</scope>
            </dependency>
        """.trimIndent()

        pomXmlBuilder.replace(0,
                pomXmlBuilder.length,
                pomXmlBuilder.replace("""<dependency>\s*<groupId>com.atlassian.plugin<\/groupId>\s*<artifactId>atlassian-spring-scanner-annotation<\/artifactId>\s*<version>\${'$'}\{atlassian.spring.scanner.version}<\/version>\s*<scope>compile<\/scope>\s*<\/dependency>""".toRegex(setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)), template))

        printProcess("pom.xml: Changing atlassian-spring-scanner dependencies in <dependencies> has been done!")
    }

    private fun fixSpringScannerXml(pluginPath: Path) {
        val xmlFolder = pluginPath.resolve("src").resolve("main").resolve("resources").resolve("META-INF").resolve("spring")
        val xmlFile = Files.list(xmlFolder).findFirst().filter { it.toString().endsWith(".xml") }.get()

        val xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <beans xmlns="http://www.springframework.org/schema/beans"
                   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xmlns:atlassian-scanner="http://www.atlassian.com/schema/atlassian-scanner/2"
                   xsi:schemaLocation="http://www.springframework.org/schema/beans
                   http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
                   http://www.atlassian.com/schema/atlassian-scanner/2
                   http://www.atlassian.com/schema/atlassian-scanner/2/atlassian-scanner.xsd">
                <atlassian-scanner:scan-indexes/>
            </beans>
        """.trimIndent()

        xmlFile.toFile().writeBytes(xmlContent.toByteArray())

        printProcess("Changing $xmlFile xml for atlassian spring scanner version 2 has been done!")
    }

    private fun addSpringFrameworkDependency(pomXmlBuilder: StringBuilder) {

        val template = """
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-context</artifactId>
                <version>${"$"}{springframework.version}</version>
                <scope>provided</scope>
            </dependency>
        """.trimIndent()

        pomXmlBuilder.insert(pomXmlBuilder.indexOf("</dependencies>"), template)

        printProcess("pom.xml: Adding org.springframework:spring-context dependency in <dependencies> has been done!")
    }

    private fun setSpringVersion(pomXmlBuilder: StringBuilder, springFrameworkVersion: String) {
        val template = "<springframework.version>$springFrameworkVersion</springframework.version>"

        pomXmlBuilder.insert( pomXmlBuilder.indexOf("</properties>"), template)

        printProcess("pom.xml: Setting <springframework.version> in <properties> has been done!")
    }

    override fun execute(pluginPath: Path, pluginData: PluginData,  pomXmlBuilder: StringBuilder) {
        if (!readYesOrNoWithQuestion("Do you want to change Atlassian Spring Scanner version 2? (Default: ${process.toYesOrNo()})", process))
            return

        val springScannerVersion = readStringWithQuestion("Atlassian spring scanner version (${defSpringScannerVersion})", defSpringScannerVersion)
        val springFrameworkVersion = readStringWithQuestion("Spring framework version (${defSpringFrameworkVersion})", defSpringFrameworkVersion)

        setAtlassianSpringVersion(pomXmlBuilder, springScannerVersion)
        fixAtlassianSpringScannerDependencies(pomXmlBuilder)
        fixSpringScannerXml(pluginPath)
        addSpringFrameworkDependency(pomXmlBuilder)
        setSpringVersion(pomXmlBuilder, springFrameworkVersion)
    }
}