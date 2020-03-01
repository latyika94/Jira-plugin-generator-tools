package org.tools.atlassian.plugin.func

import org.tools.atlassian.plugin.config.ApplicationProperty
import org.tools.atlassian.plugin.utils.KeyWords

fun String?.resolveNullOrEmpty(defaultValue: String): String {
    return if (this.isNullOrBlank())
        defaultValue
    else
        this.trim()
}

fun String?.resolveYesOrNo(default: Boolean): Boolean {
    val defaultString = if(default) "yes" else "no"
    return this.resolveNullOrEmpty(defaultString).toLowerCase() == "yes"
}

fun printQuestion(question: String) {
    print("${KeyWords.question} $question: ")
}

fun printProcess(process: String) {
    print("${KeyWords.process}: $process")
    println()
}

fun readStringWithQuestion(question: String, default: String): String {
    return if(!ApplicationProperty.getBoolean("silentmode", false)) {
        printQuestion(question)
        readLine().resolveNullOrEmpty(default).let {
            println()
            it
        }
    } else default
}

fun readYesOrNoWithQuestion(question: String, default: Boolean): Boolean {
    return if(!ApplicationProperty.getBoolean("silentmode", false)) {
        printQuestion(KeyWords.questionYesOrNo + question)
        readLine().resolveYesOrNo(default).let {
            println()
            it
        }
    } else default
}

fun String.escapeXml(): String = this.replace("&", "&amp;")
        .replace(">", "&gt;").replace("<", "&lt;").replace("\"", "&quot;").replace("'", "&apos;");

fun Boolean.toYesOrNo(): String = if(this) "YES" else "NO"