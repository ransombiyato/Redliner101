package com.ransombiyato.demiforge.core.adapters

import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Path

class DummyGameAdapter : GameAdapter {
    override val gameId = "dummy-game"
    override val displayName = "Dummy Game Environment"

    override fun inspect(candidateRoot: Path?): GameInspection {
        if (candidateRoot == null || !Files.isDirectory(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_FOUND,
            message = "Create or select the harmless dummy-game directory to test the complete mod pipeline."
        )
        val descriptor = candidateRoot.resolve("dummy-game.json")
        if (!Files.isRegularFile(descriptor)) return GameInspection(
            gameId, displayName, IntegrationStatus.NOT_FOUND,
            message = "The selected directory is not a DemiForge dummy-game environment."
        )
        val version = JSONObject(Files.readString(descriptor)).optString("version", "0.0.0")
        val data = candidateRoot.resolve("data")
        if (!Files.isWritable(candidateRoot)) return GameInspection(
            gameId, displayName, IntegrationStatus.READ_ONLY, version, data,
            "Dummy game data exists but is not writable."
        )
        return GameInspection(gameId, displayName, IntegrationStatus.READY, version, data, "Dummy game is ready for safe overlay and copy-patch tests.")
    }

    override fun supportsPatching(inspection: GameInspection): Boolean = inspection.status == IntegrationStatus.READY
    override fun patchRoot(inspection: GameInspection): Path? = inspection.dataRoot
}
