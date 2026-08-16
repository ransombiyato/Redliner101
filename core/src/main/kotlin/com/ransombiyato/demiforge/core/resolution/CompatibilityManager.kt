package com.ransombiyato.demiforge.core.resolution

import com.ransombiyato.demiforge.core.model.ModManifest

object CompatibilityManager {
    fun supportsGameVersion(manifest: ModManifest, gameVersion: String): Boolean =
        manifest.supportedGameVersions.any { rule -> supportsVersion(rule, gameVersion) }

    fun supportsVersion(rule: String, actual: String): Boolean = when {
        rule == "*" -> true
        rule.startsWith(">=") -> compare(actual, rule.removePrefix(">=")) >= 0
        rule.endsWith(".*") -> actual.startsWith(rule.removeSuffix("*"))
        else -> actual == rule
    }

    fun atLeast(actual: String, minimum: String): Boolean = compare(actual, minimum) >= 0

    private fun compare(left: String, right: String): Int {
        val lhs = left.split(Regex("[.-]")).mapNotNull { it.toIntOrNull() }
        val rhs = right.split(Regex("[.-]")).mapNotNull { it.toIntOrNull() }
        val max = maxOf(lhs.size, rhs.size)
        for (index in 0 until max) {
            val difference = (lhs.getOrElse(index) { 0 }).compareTo(rhs.getOrElse(index) { 0 })
            if (difference != 0) return difference
        }
        return 0
    }
}
