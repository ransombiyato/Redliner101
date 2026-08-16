package com.ransombiyato.demiforge.core.adapters

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

/**
 * A payload is deliberately identified by its visible filename only. DemiForge never reads game
 * contents when it inventories a workspace; the real mod operation works only on a payload path
 * the user reviewed and confirmed through the Android document picker.
 */
enum class DeltarunePayloadKind {
    GAME_DROID,
    DATA_DROID,
    WAD,
}

data class DeltarunePayload(
    val relativePath: String,
    val sizeBytes: Long,
    val kind: DeltarunePayloadKind,
)

data class DeltaruneWorkspace(
    val payloads: List<DeltarunePayload>,
    /** Changes when a payload is added, removed, renamed, or changes kind. */
    val layoutFingerprint: String,
    /** Changes when the visible payload sizes change; intended for inventory and backup records. */
    val contentFingerprint: String,
) {
    val recognised: Boolean get() = payloads.isNotEmpty()
}

/**
 * Shared recognizer for the user-selected external file workspace used by Android Deltarune ports.
 * It accepts only known Android GameMaker payload names and never guesses an app package name.
 */
object DeltaruneWorkspaceInspector {
    const val HADRIAN_VERSION_ID = "hadrian-android"
    private const val MAX_DEPTH = 6

    fun payloadKind(name: String): DeltarunePayloadKind? = when {
        name.equals("game.droid", ignoreCase = true) -> DeltarunePayloadKind.GAME_DROID
        name.equals("data.droid", ignoreCase = true) -> DeltarunePayloadKind.DATA_DROID
        name.endsWith(".wad", ignoreCase = true) -> DeltarunePayloadKind.WAD
        else -> null
    }

    fun inspect(root: Path): DeltaruneWorkspace {
        require(Files.isDirectory(root)) { "Workspace is not a directory: $root" }
        val payloads = Files.walk(root, MAX_DEPTH).use { paths ->
            val candidates = mutableListOf<DeltarunePayload>()
            paths.filter { path -> Files.isRegularFile(path) }.forEach { file: Path ->
                val kind = payloadKind(file.fileName.toString())
                if (kind != null) {
                    candidates += DeltarunePayload(
                        relativePath = root.relativize(file).normalize().toString().replace('\\', '/'),
                        sizeBytes = Files.size(file),
                        kind = kind,
                    )
                }
            }
            candidates.sortedBy { payload: DeltarunePayload -> payload.relativePath.lowercase() }
        }
        return DeltaruneWorkspace(
            payloads = payloads,
            layoutFingerprint = fingerprint(payloads, includeSize = false),
            contentFingerprint = fingerprint(payloads, includeSize = true),
        )
    }

    fun fingerprint(payloads: List<DeltarunePayload>, includeSize: Boolean = true): String {
        val inventory = payloads
            .sortedBy { it.relativePath.lowercase() }
            .joinToString("\n") { payload ->
                "${payload.relativePath.lowercase()}|${if (includeSize) payload.sizeBytes else "layout"}|${payload.kind.name}"
            }
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(inventory.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }.take(16)
    }
}
