package org.tools.atlassian.plugin

data class PluginData (
        val groupId: String,
        val artifactId: String,
        val version: String,
        val packagePath: String
)