package com.ransombiyato.demiforge.core.adapters

import java.nio.file.Files
import java.nio.file.Path

/**
 * An Android-port adapter that inspects only a directory explicitly supplied by the user. It does
 * not enumerate packages, access private app storage, or alter an APK. A matching payload must be
 * further confirmed by the user-facing Storage Access Framework flow before it can be patched.
 */
class DeltaruneAdapter : GameAdapter {
    override val gameId = "deltarune-hadrian-android"
    override val displayName = "Deltarune — Hadrian Android Port"

    override fun inspect(candidateRoot: Path?): GameInspection {
        if (candidateRoot == null) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_CONFIGURED,
            message = "Choose Hadrian's externally accessible port workspace. DemiForge will not probe protected app storage or alter APK files."
        )
        if (!Files.isDirectory(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_FOUND,
            message = "The selected location does not exist or is not a directory."
        )
        if (!Files.isReadable(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.READ_ONLY,
            message = "The selected location is not readable by DemiForge."
        )
        val workspace = DeltaruneWorkspaceInspector.inspect(candidateRoot)
        if (!workspace.recognised) return GameInspection(
            gameId = gameId,
            displayName = displayName,
            status = IntegrationStatus.LIMITED,
            dataRoot = candidateRoot,
            message = "No game.droid, data.droid, or WAD payload was found in the selected directory. DemiForge will not guess a patch target."
        )
        return GameInspection(
            gameId = gameId,
            displayName = displayName,
            status = if (Files.isWritable(candidateRoot)) IntegrationStatus.READY else IntegrationStatus.READ_ONLY,
            version = DeltaruneWorkspaceInspector.HADRIAN_VERSION_ID,
            dataRoot = candidateRoot,
            message = if (Files.isWritable(candidateRoot)) {
                "Found ${workspace.payloads.size} Android Deltarune payload candidate(s). Review and confirm workspace ${workspace.layoutFingerprint} before applying a compatible mod."
            } else {
                "Found ${workspace.payloads.size} Android Deltarune payload candidate(s), but the selected directory is read-only."
            }
        )
    }

    override fun supportsPatching(inspection: GameInspection): Boolean =
        inspection.status == IntegrationStatus.READY && inspection.dataRoot != null

    override fun patchRoot(inspection: GameInspection): Path? = inspection.dataRoot
}
