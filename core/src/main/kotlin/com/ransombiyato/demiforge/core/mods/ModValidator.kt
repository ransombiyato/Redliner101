package com.ransombiyato.demiforge.core.mods

import com.ransombiyato.demiforge.core.model.IssueSeverity
import com.ransombiyato.demiforge.core.model.ModIssue
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.ValidationResult

class ModValidator {
    private val idPattern = Regex("[a-z][a-z0-9_.-]{2,63}")
    private val versionPattern = Regex("[0-9]+(\\.[0-9A-Za-z-]+){0,3}")

    fun validate(manifest: ModManifest): ValidationResult {
        val issues = buildList {
            if (manifest.schemaVersion != 1) add(error("unsupported_schema", "Only manifest schemaVersion 1 is supported", manifest.id))
            if (!idPattern.matches(manifest.id)) add(error("invalid_id", "Mod ID must match ${idPattern.pattern}", manifest.id))
            if (!versionPattern.matches(manifest.version)) add(error("invalid_version", "Mod version must be numeric semantic-style text", manifest.id))
            if (manifest.targetGame.isBlank()) add(error("missing_target", "A target game is required", manifest.id))
            if (manifest.supportedGameVersions.isEmpty()) add(error("missing_game_versions", "At least one supported game version is required", manifest.id))
            manifest.dependencies.filter { !idPattern.matches(it.id) }.forEach { add(error("invalid_dependency", "Invalid dependency ID '${it.id}'", manifest.id)) }
            manifest.conflicts.filter { !idPattern.matches(it.id) }.forEach { add(error("invalid_conflict", "Invalid conflict ID '${it.id}'", manifest.id)) }
            manifest.patches.forEach { patch ->
                if (patch.source.startsWith("/") || patch.source.contains("..")) add(error("unsafe_source", "Patch source must be relative and cannot traverse directories", manifest.id))
                if (patch.target.startsWith("/") || patch.target.contains("..")) add(error("unsafe_target", "Patch target must be relative and cannot traverse directories", manifest.id))
            }
        }
        return ValidationResult(manifest, issues)
    }

    fun validateText(text: String): ValidationResult = try {
        validate(ModManifestCodec.parse(text))
    } catch (exception: Exception) {
        ValidationResult(null, listOf(ModIssue(IssueSeverity.ERROR, "invalid_manifest", exception.message ?: "Malformed manifest")))
    }

    private fun error(code: String, message: String, modId: String) = ModIssue(IssueSeverity.ERROR, code, message, modId)
}
