package com.ransombiyato.demiforge.core.adapters

import java.nio.file.Path

enum class IntegrationStatus { NOT_CONFIGURED, NOT_FOUND, READ_ONLY, LIMITED, READY, UNSUPPORTED }

data class GameInspection(
    val gameId: String,
    val displayName: String,
    val status: IntegrationStatus,
    val version: String? = null,
    val dataRoot: Path? = null,
    val message: String,
)

interface GameAdapter {
    val gameId: String
    val displayName: String
    fun inspect(candidateRoot: Path?): GameInspection
    fun supportsPatching(inspection: GameInspection): Boolean
    fun patchRoot(inspection: GameInspection): Path?
}
