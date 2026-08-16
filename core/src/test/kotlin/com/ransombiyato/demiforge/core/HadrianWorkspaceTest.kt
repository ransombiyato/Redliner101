package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.adapters.DeltaruneAdapter
import com.ransombiyato.demiforge.core.adapters.DeltarunePayloadKind
import com.ransombiyato.demiforge.core.adapters.DeltaruneWorkspaceInspector
import com.ransombiyato.demiforge.core.adapters.IntegrationStatus
import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.PatchMode
import com.ransombiyato.demiforge.core.model.PatchOperation
import com.ransombiyato.demiforge.core.mods.ModManifestCodec
import com.ransombiyato.demiforge.core.recovery.BackupManager
import com.ransombiyato.demiforge.core.storage.ModStorage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HadrianWorkspaceTest {
    @TempDir lateinit var temp: Path

    @Test fun `recognizer inventories Android payload candidates without reading their contents`() {
        val workspace = temp.resolve("hadrian-workspace")
        Files.createDirectories(workspace.resolve("chapter3_windows"))
        Files.write(workspace.resolve("chapter3_windows/game.droid"), byteArrayOf(1, 2, 3))
        Files.write(workspace.resolve("chapter4.wad"), byteArrayOf(4, 5))
        Files.writeString(workspace.resolve("notes.txt"), "not a payload")

        val inspection = DeltaruneWorkspaceInspector.inspect(workspace)

        assertTrue(inspection.recognised)
        assertEquals(2, inspection.payloads.size)
        assertEquals(DeltarunePayloadKind.GAME_DROID, inspection.payloads.first { it.relativePath.endsWith("game.droid") }.kind)
        assertEquals(DeltarunePayloadKind.WAD, inspection.payloads.first { it.relativePath.endsWith(".wad") }.kind)
        assertEquals(16, inspection.layoutFingerprint.length)
        assertEquals(16, inspection.contentFingerprint.length)
    }

    @Test fun `adapter enables local patch pipeline only for a selected recognised writable workspace`() {
        val empty = temp.resolve("empty")
        Files.createDirectories(empty)
        val valid = temp.resolve("valid")
        Files.createDirectories(valid)
        Files.write(valid.resolve("data.droid"), byteArrayOf(9))
        val adapter = DeltaruneAdapter()

        assertEquals(IntegrationStatus.LIMITED, adapter.inspect(empty).status)
        val detected = adapter.inspect(valid)
        assertEquals(IntegrationStatus.READY, detected.status)
        assertTrue(adapter.supportsPatching(detected))
        assertFalse(adapter.supportsPatching(adapter.inspect(null)))
    }

    @Test fun `Hadrian payload fixture supports a real replacement and original-file recovery`() {
        val workspace = temp.resolve("HadrianExternalFiles")
        val target = workspace.resolve("chapter3_windows/game.droid")
        Files.createDirectories(target.parent)
        Files.writeString(target, "original Android-ready payload")

        val adapter = DeltaruneAdapter()
        val source = temp.resolve("mod-source")
        Files.createDirectories(source.resolve("payload"))
        Files.writeString(source.resolve("payload/game.droid"), "modded Android-ready payload")
        Files.writeString(source.resolve("manifest.json"), ModManifestCodec.encode(
            ModManifest(
                id = "real-port-fixture",
                name = "Real port fixture",
                author = "test",
                version = "1.0.0",
                description = "No game data; only a structural fixture.",
                targetGame = adapter.gameId,
                supportedGameVersions = listOf(DeltaruneWorkspaceInspector.HADRIAN_VERSION_ID),
                patches = listOf(PatchOperation("payload/game.droid", "chapter3_windows/game.droid", PatchMode.COPY)),
            )
        ))
        val storage = ModStorage(temp.resolve("state"), EventLog())
        assertTrue(storage.install(source).valid)
        storage.setEnabled("real-port-fixture", true)
        val backups = BackupManager(temp.resolve("backups"))
        val original = backups.backup(workspace, listOf("chapter3_windows/game.droid"))
        val manager = ModManager(storage, backups, EventLog())

        assertTrue(manager.apply(adapter, adapter.inspect(workspace)).applied)
        assertEquals("modded Android-ready payload", Files.readString(target))

        backups.restore(workspace, original)
        assertEquals("original Android-ready payload", Files.readString(target))

        storage.setEnabled("real-port-fixture", false)
        assertTrue(manager.apply(adapter, adapter.inspect(workspace)).applied)
        assertEquals("original Android-ready payload", Files.readString(target))

        storage.setEnabled("real-port-fixture", true)
        assertTrue(manager.apply(adapter, adapter.inspect(workspace)).applied)
        assertEquals("modded Android-ready payload", Files.readString(target))
    }
}
