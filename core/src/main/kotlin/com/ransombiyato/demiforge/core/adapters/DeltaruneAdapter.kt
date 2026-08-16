package com.ransombiyato.demiforge.core.adapters

import java.nio.file.Files
import java.nio.file.Path

/**
 * A deliberately conservative Android-side adapter. It neither enumerates installed packages nor
 * accesses private app storage. It only evaluates a directory explicitly supplied by the user.
 */
class DeltaruneAdapter : GameAdapter {
    override val gameId = "deltarune"
    override val displayName = "Deltarune"

    override fun inspect(candidateRoot: Path?): GameInspection {
        if (candidateRoot == null) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_CONFIGURED,
            message = "No user-selected Deltarune data location is configured. DemiForge will not probe protected app storage or alter APK files."
        )
        if (!Files.isDirectory(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_FOUND,
            message = "The selected location does not exist or is not a directory."
        )
        if (!Files.isReadable(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.READ_ONLY,
            message = "The selected location is not readable by DemiForge."
        )
        val metadata = candidateRoot.resolve("demiforge-deltarune-data.json")
        return GameInspection(
            gameId = gameId,
            displayName = displayName,
            status = IntegrationStatus.LIMITED,
            dataRoot = candidateRoot,
            message = if (Files.isRegularFile(metadata)) {
                "An externally supplied Deltarune data location was selected. No safe Android patch mechanism has been validated for this game yet; modification is disabled."
            } else {
                "The selected directory is accessible, but no supported Deltarune integration marker was found. Modification is disabled rather than guessed."
            }
        )
    }

    override fun supportsPatching(inspection: GameInspection): Boolean = false
    override fun patchRoot(inspection: GameInspection): Path? = null
}
