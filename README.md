# Jira-plugin-generator-tools
This little CLI tool helps to modify the generated atlassian jira addon by Atlassian SDK `atlas-create-jira-plugin` command. When using the tool you will be asked that you want to do the step (listing below) or skip it. 

**The tool can do the following things**
 - Generate default jira addon skeleton by Atlassian SDK `atlas-create-jira-plugin` command
 - Setting Jira application version
 - Add Jira ServiceDesk application to pom.xml
 - Change organization name and url
 - Setting maven compiler version and plugin
 - Add default release and snapshot distributionManagement
 - Change Atlassian Spring Scanner version from 1.x to 2.x
 - Add kotlin language support to addon
 - Set plugin icon and logo
 - Move plugin properties to i18n directory

**Prerequisite of usage this tool**
 - Windows Operation System (tool use `cmd.exe`)
 - Atlassian SDK 8.X.X (Tested on 8.0.16)
 - Java 8 or higher

**Start using tool**
 1. Open command line tool (eq. `cmd` or `powershell`)
 2. Run `java -jar jira-plugin-generator-tools-1.0.0-jar-with-dependencies.jar`
 3. Type parameter what you ask for in CLI (press **Enter** for use default value)

**Define default values**

You can place an `application.properties` file next to `.jar` file where you define the above steps properties (see [application.properties](https://github.com/latyika94/Jira-plugin-generator-tools/blob/master/src/main/resources/org/tools/atlassian/plugin/config/application.properties)) for all possible settings. The tool can running in silentmode if you set `silentmode` property to `true`. In this mode the tool won't ask you to give parameters by command line, it will use the defined values from `application.properties` file.
