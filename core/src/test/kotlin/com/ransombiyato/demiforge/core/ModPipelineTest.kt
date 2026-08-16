package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.adapters.DummyGameAdapter
import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.ModConflict
import com.ransombiyato.demiforge.core.model.ModDependency
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.PatchMode
import com.ransombiyato.demiforge.core.model.PatchOperation
import com.ransombiyato.demiforge.core.mods.ModManifestCodec
import com.ransombiyato.demiforge.core.mods.ModValidator
import com.ransombiyato.demiforge.core.recovery.BackupManager
import com.ransombiyato.demiforge.core.resolution.DependencyResolver
import com.ransombiyato.demiforge.core.storage.ModStorage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModPipelineTest {
    @TempDir lateinit var temp: Path

    @Test fun `manifest parser and validator accept a complete mod`() {
        val manifest = manifest("valid-mod", patches = listOf(PatchOperation("payload/a.txt", "content/a.txt")))
        val result = ModValidator().validateText(ModManifestCodec.encode(manifest))
        assertTrue(result.valid)
        assertEquals("valid-mod", result.manifest?.id)
    }

    @Test fun `validator rejects traversal and malformed mod identifiers`() {
        val invalid = manifest("Bad ID", patches = listOf(PatchOperation("../escape", "content/file")))
        val result = ModValidator().validate(invalid)
        assertFalse(result.valid)
        assertTrue(result.issues.any { it.code == "invalid_id" })
        assertTrue(result.issues.any { it.code == "unsafe_source" })
    }

    @Test fun `resolver orders dependencies and detects conflicts missing dependencies and cycles`() {
        val base = manifest("base")
        val addon = manifest("addon", dependencies = listOf(ModDependency("base")), loadAfter = listOf("base"))
        val resolved = DependencyResolver().resolve(listOf(addon, base), "dummy-game", "1.0.0")
        assertTrue(resolved.valid)
        assertEquals(listOf("base", "addon"), resolved.loadOrder.map { it.id })

        val missing = DependencyResolver().resolve(listOf(manifest("needs-other", dependencies = listOf(ModDependency("missing")))), "dummy-game", "1.0.0")
        assertFalse(missing.valid)
        assertTrue(missing.issues.any { it.code == "missing_dependency" })

        val conflicting = DependencyResolver().resolve(listOf(manifest("one", conflicts = listOf(ModConflict("two"))), manifest("two")), "dummy-game", "1.0.0")
        assertFalse(conflicting.valid)
        assertTrue(conflicting.issues.any { it.code == "conflict" })

        val cycle = DependencyResolver().resolve(listOf(manifest("alpha", loadAfter = listOf("beta")), manifest("beta", loadAfter = listOf("alpha"))), "dummy-game", "1.0.0")
        assertFalse(cycle.valid)
        assertTrue(cycle.issues.any { it.code == "circular_dependency" })
    }

    @Test fun `storage installs discovers and toggles mods`() {
        val storage = storage()
        val source = createMod("toggle-mod")
        assertTrue(storage.install(source).valid)
        assertFalse(storage.discover().single().enabled)
        storage.setEnabled("toggle-mod", true)
        assertTrue(storage.discover().single().enabled)
        storage.remove("toggle-mod")
        assertTrue(storage.discover().isEmpty())
    }

    @Test fun `dummy adapter applies overlay patches without changing base data`() {
        val storage = storage()
        storage.install(createMod("overlay-mod", patchMode = PatchMode.OVERLAY, body = "overlay text"))
        storage.setEnabled("overlay-mod", true)
        val root = dummyGame()
        val manager = manager(storage)
        val adapter = DummyGameAdapter()
        val result = manager.apply(adapter, adapter.inspect(root))
        assertTrue(result.applied)
        assertEquals("base text", Files.readString(root.resolve("data/content/message.txt")))
        assertEquals("overlay text", Files.readString(root.resolve("data/.demiforge-overlay/content/message.txt")))
    }

    @Test fun `copy patch makes backup and failed patch recovers original data`() {
        val storage = storage()
        storage.install(createMod("copy-mod", patchMode = PatchMode.COPY, body = "changed text"))
        storage.setEnabled("copy-mod", true)
        val root = dummyGame()
        val manager = manager(storage)
        val adapter = DummyGameAdapter()
        assertTrue(manager.apply(adapter, adapter.inspect(root)).applied)
        assertEquals("changed text", Files.readString(root.resolve("data/content/message.txt")))

        storage.install(createMod("broken-mod", sourceName = "missing.txt", patchMode = PatchMode.COPY))
        storage.setEnabled("broken-mod", true)
        val failed = manager.apply(adapter, adapter.inspect(root))
        assertFalse(failed.applied)
        assertEquals("changed text", Files.readString(root.resolve("data/content/message.txt")))
    }

    @Test fun `safe mode prevents every patch operation`() {
        val storage = storage()
        storage.install(createMod("safe-mode-mod", patchMode = PatchMode.COPY, body = "should not apply"))
        storage.setEnabled("safe-mode-mod", true)
        val root = dummyGame()
        val manager = manager(storage)
        manager.setSafeMode(true)
        val adapter = DummyGameAdapter()
        assertFalse(manager.apply(adapter, adapter.inspect(root)).applied)
        assertEquals("base text", Files.readString(root.resolve("data/content/message.txt")))
    }

    private fun storage(): ModStorage = ModStorage(temp.resolve("state"), EventLog())
    private fun manager(storage: ModStorage): ModManager = ModManager(storage, BackupManager(temp.resolve("state/backups")), EventLog())

    private fun dummyGame(): Path {
        val root = temp.resolve("dummy-game")
        Files.createDirectories(root.resolve("data/content"))
        Files.writeString(root.resolve("dummy-game.json"), "{\"version\":\"1.0.0\"}")
        Files.writeString(root.resolve("data/content/message.txt"), "base text")
        return root
    }

    private fun createMod(id: String, sourceName: String = "message.txt", patchMode: PatchMode = PatchMode.OVERLAY, body: String = "payload"): Path {
        val root = temp.resolve("sources").resolve(id)
        Files.createDirectories(root.resolve("payload"))
        if (sourceName != "missing.txt") Files.writeString(root.resolve("payload/$sourceName"), body)
        val mod = manifest(id, patches = listOf(PatchOperation("payload/$sourceName", "content/message.txt", patchMode)))
        Files.writeString(root.resolve("manifest.json"), ModManifestCodec.encode(mod))
        return root
    }

    private fun manifest(
        id: String,
        dependencies: List<ModDependency> = emptyList(),
        conflicts: List<ModConflict> = emptyList(),
        loadAfter: List<String> = emptyList(),
        patches: List<PatchOperation> = emptyList(),
    ) = ModManifest(
        id = id,
        name = id,
        author = "test",
        version = "1.0.0",
        description = "test mod",
        targetGame = "dummy-game",
        supportedGameVersions = listOf("1.0.0"),
        dependencies = dependencies,
        conflicts = conflicts,
        loadAfter = loadAfter,
        patches = patches,
    )
}
