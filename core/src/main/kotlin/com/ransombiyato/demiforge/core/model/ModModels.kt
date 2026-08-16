package com.ransombiyato.demiforge.core.model

enum class PatchMode { OVERLAY, COPY }

data class ModDependency(
    val id: String,
    val minVersion: String? = null,
    val required: Boolean = true,
)

data class ModConflict(val id: String, val reason: String = "")

data class PatchOperation(
    val source: String,
    val target: String,
    val mode: PatchMode = PatchMode.OVERLAY,
)

data class ModManifest(
    val schemaVersion: Int = 1,
    val id: String,
    val name: String,
    val author: String,
    val version: String,
    val description: String,
    val targetGame: String,
    val supportedGameVersions: List<String> = listOf("*"),
    val dependencies: List<ModDependency> = emptyList(),
    val conflicts: List<ModConflict> = emptyList(),
    val loadAfter: List<String> = emptyList(),
    val loadBefore: List<String> = emptyList(),
    val patches: List<PatchOperation> = emptyList(),
    val configuration: Map<String, String> = emptyMap(),
)

data class InstalledMod(
    val manifest: ModManifest,
    val installDirectory: String,
    val enabled: Boolean,
)

enum class IssueSeverity { ERROR, WARNING, INFO }

data class ModIssue(
    val severity: IssueSeverity,
    val code: String,
    val message: String,
    val modId: String? = null,
)

data class ValidationResult(
    val manifest: ModManifest? = null,
    val issues: List<ModIssue> = emptyList(),
) {
    val valid: Boolean get() = manifest != null && issues.none { it.severity == IssueSeverity.ERROR }
}

data class ResolutionResult(
    val loadOrder: List<ModManifest>,
    val issues: List<ModIssue>,
) {
    val valid: Boolean get() = issues.none { it.severity == IssueSeverity.ERROR }
}
