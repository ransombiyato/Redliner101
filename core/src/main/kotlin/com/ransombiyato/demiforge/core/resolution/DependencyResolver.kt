package com.ransombiyato.demiforge.core.resolution

import com.ransombiyato.demiforge.core.model.IssueSeverity
import com.ransombiyato.demiforge.core.model.ModIssue
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.ResolutionResult

class DependencyResolver {
    fun resolve(enabled: Collection<ModManifest>, gameId: String, gameVersion: String): ResolutionResult {
        val manifests = enabled.associateBy { it.id }
        val issues = mutableListOf<ModIssue>()
        val edges = manifests.keys.associateWith { linkedSetOf<String>() }.toMutableMap()

        manifests.values.sortedBy { it.id }.forEach { mod ->
            if (mod.targetGame != gameId) issues += error("wrong_game", "Targets ${mod.targetGame}, not $gameId", mod.id)
            if (!CompatibilityManager.supportsGameVersion(mod, gameVersion)) issues += error("incompatible_game_version", "Does not support game version $gameVersion", mod.id)
            mod.dependencies.forEach { dependency ->
                val found = manifests[dependency.id]
                if (found == null && dependency.required) issues += error("missing_dependency", "Requires ${dependency.id}", mod.id)
                if (found != null && dependency.minVersion != null && !CompatibilityManager.atLeast(found.version, dependency.minVersion)) {
                    issues += error("dependency_version", "Requires ${dependency.id} >= ${dependency.minVersion}", mod.id)
                }
                if (found != null) edges.getValue(found.id).add(mod.id)
            }
            mod.conflicts.filter { manifests.containsKey(it.id) }.forEach { conflict ->
                issues += error("conflict", "Conflicts with ${conflict.id}${if (conflict.reason.isBlank()) "" else ": ${conflict.reason}"}", mod.id)
            }
            mod.loadAfter.filter { manifests.containsKey(it) }.forEach { before -> edges.getValue(before).add(mod.id) }
            mod.loadBefore.filter { manifests.containsKey(it) }.forEach { after -> edges.getValue(mod.id).add(after) }
        }

        if (issues.any { it.severity == IssueSeverity.ERROR }) return ResolutionResult(emptyList(), issues)
        val incoming = manifests.keys.associateWith { 0 }.toMutableMap()
        edges.values.flatten().forEach { target -> incoming[target] = incoming.getValue(target) + 1 }
        val ready = java.util.PriorityQueue<String>().apply { addAll(incoming.filterValues { it == 0 }.keys) }
        val order = mutableListOf<ModManifest>()
        while (ready.isNotEmpty()) {
            val id = ready.remove()
            order += manifests.getValue(id)
            edges.getValue(id).sorted().forEach { next ->
                incoming[next] = incoming.getValue(next) - 1
                if (incoming.getValue(next) == 0) ready += next
            }
        }
        if (order.size != manifests.size) issues += ModIssue(IssueSeverity.ERROR, "circular_dependency", "A dependency or load-order cycle was found")
        return ResolutionResult(if (issues.any { it.severity == IssueSeverity.ERROR }) emptyList() else order, issues)
    }

    private fun error(code: String, message: String, modId: String) = ModIssue(IssueSeverity.ERROR, code, message, modId)
}
