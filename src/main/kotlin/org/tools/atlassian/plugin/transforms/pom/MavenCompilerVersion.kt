package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import java.nio.file.Path

object MavenCompilerVersion : IPomXmlTransformation {
    private val mavenDefCompilerVersion = ApplicationProperty.getString("maven.version.compiler", "3.8.1")

    private fun addMavenCompilerProperties(pomString: StringBuilder, mavenCompilerVersion: String) {
        val template = "<maven.compiler.version>$mavenCompilerVersion</maven.compiler.version>"

        pomString.insert( pomString.indexOf("</properties>"), template)

        printProcess("pom.xml: Setting <maven.compiler.version> in <properties> has been done!")
    }

    private fun addMavenCompilerPlugin(pomString: StringBuilder) {
        val template = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${'$'}{maven.compiler.version}</version>
                <executions>        <!-- Replacing default-compile as it is treated specially by maven -->
                    <execution>
                        <id>default-compile</id>
                        <phase>none</phase>
                    </execution>        <!-- Replacing default-testCompile as it is treated specially by maven -->
                    <execution>
                        <id>default-testCompile</id>
                        <phase>none</phase>
                    </execution>
                    <execution>
                        <id>java-compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>java-test-compile</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        """.trimIndent()

        pomString.insert( pomString.indexOf("</plugins>"), template)

        printProcess("pom.xml: Adding maven-compiler-plugin plugin in <plugins> has been done!")
    }

    override fun execute(pluginPath: Path, pluginData: PluginData, pomXmlBuilder: StringBuilder) {
        val mavenCompilerVersion = readStringWithQuestion("Maven compiler version (org.apache.maven.plugins:maven-compiler-plugin)? ($mavenDefCompilerVersion)", mavenDefCompilerVersion)

        addMavenCompilerProperties(pomXmlBuilder, mavenCompilerVersion)
        addMavenCompilerPlugin(pomXmlBuilder)
    }
}