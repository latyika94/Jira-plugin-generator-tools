package org.tools.atlassian.plugin.transforms.pom

import org.tools.atlassian.plugin.PluginData
import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.func.printProcess
import org.tools.atlassian.plugin.func.readStringWithQuestion
import org.tools.atlassian.plugin.func.readYesOrNoWithQuestion
import org.tools.atlassian.plugin.func.toYesOrNo
import java.nio.file.Path

object KotlinSupport : IPomXmlTransformation {
    private val kotlinDefVersion = ApplicationProperty.getString("kotlin.version", "1.3.61")
    private val kotlinDefLanguageVersion = ApplicationProperty.getString("kotlin.languageVersion", "1.3")

    private val process = ApplicationProperty.getBoolean("kotlin.process", false)
    private val processPackage = ApplicationProperty.getBoolean("kotlin.process.package", false)

    private fun addKotlinProperties(pomString: StringBuilder, kotlinVersion: String, kotlinLanguageVersion: String) {
        val template = """
         <kotlin.version>$kotlinVersion</kotlin.version>
         <kotlin.compiler.incremental>true</kotlin.compiler.incremental> 
         <kotlin.compiler.languageVersion>$kotlinLanguageVersion</kotlin.compiler.languageVersion>
        """.trimIndent()

        pomString.insert(pomString.indexOf("</properties>"), template)

        printProcess("pom.xml: Setting kotlin language properties in <properties> has been done!")
    }

    private fun addKotlinStdLib(pomString: StringBuilder) {
        val template = """
            <dependency>    
               <groupId>org.jetbrains.kotlin</groupId>    
               <artifactId>kotlin-stdlib</artifactId>    
               <version>${'$'}{kotlin.version}</version> 
            </dependency>
        """.trimIndent()

        pomString.insert(pomString.indexOf("</dependencies>"), template)

        printProcess("pom.xml: Adding org.jetbrains.kotlin:kotlin-stdlib dependency in <dependencies> has been done!")
    }

    private fun addKotlinCompiler(pomString: StringBuilder) {
        val template = """
            <plugin>
                <groupId>org.jetbrains.kotlin</groupId>
                <artifactId>kotlin-maven-plugin</artifactId>
                <version>${'$'}{kotlin.version}</version>
                <executions>
                    <execution>
                        <id>compile</id>
                        <phase>compile</phase>
                        <goals>
                            <goal>compile</goal>
                        </goals>
                        <configuration>
                            <sourceDirs>
                                <sourceDir>${'$'}{project.basedir}/src/main/kotlin</sourceDir>
                                <sourceDir>${'$'}{project.basedir}/src/main/java</sourceDir>
                            </sourceDirs>
                        </configuration>
                    </execution>
                    <execution>
                        <id>test-compile</id>
                        <phase>test-compile</phase>
                        <goals>
                            <goal>test-compile</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <languageVersion>${'$'}{kotlin.compiler.languageVersion}</languageVersion>
                </configuration>
            </plugin>
        """.trimIndent()
        pomString.insert(pomString.indexOf("</plugins>"), template)

        printProcess("pom.xml: Adding kotlin-maven-plugin plugin in <plugins> has been done!")
    }

    private fun createKotlinPackagePath(kotlinPackage: String, pluginPath: Path) {
        val packagePath = pluginPath.resolve("src").resolve("main").resolve("kotlin").let { p ->
            var path = p
            kotlinPackage.split(".").forEach {
                path = path.resolve(it)
            }
            path
        }

        if (packagePath.toFile().mkdirs()) {
            printProcess("Creating packages ($kotlinPackage) for kotlin is done! Path: $packagePath")
        } else {
            printProcess("Can not create packages ($kotlinPackage) for kotlin!")
        }
    }

    override fun execute(pluginPath: Path, pluginData: PluginData, pomXmlBuilder: StringBuilder) {

        if (!readYesOrNoWithQuestion("Do you need kotlin support? (Default: ${process.toYesOrNo()})", process)) return

        val kotlinVersion = readStringWithQuestion("Kotlin version ($kotlinDefVersion)", kotlinDefVersion)
        val kotlinLanguageVersion = readStringWithQuestion("Kotlin language version ($kotlinDefLanguageVersion)", kotlinDefLanguageVersion)

        addKotlinProperties(pomXmlBuilder, kotlinVersion, kotlinLanguageVersion)
        addKotlinStdLib(pomXmlBuilder)
        addKotlinCompiler(pomXmlBuilder)

        if (!readYesOrNoWithQuestion("Do you want to create base package for Kotlin (eq. com.example.test)? (Default: ${processPackage.toYesOrNo()})", processPackage)) return

        val kotlinPackage = readStringWithQuestion("Kotlin package path (${pluginData.packagePath})", pluginData.packagePath)
        createKotlinPackagePath(kotlinPackage, pluginPath)
    }
}