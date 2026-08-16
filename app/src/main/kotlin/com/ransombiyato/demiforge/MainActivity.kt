package com.ransombiyato.demiforge

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import com.ransombiyato.demiforge.core.ModManager
import com.ransombiyato.demiforge.core.adapters.DeltaruneAdapter
import com.ransombiyato.demiforge.core.adapters.DummyGameAdapter
import com.ransombiyato.demiforge.core.adapters.GameAdapter
import com.ransombiyato.demiforge.core.adapters.GameInspection
import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.InstalledMod
import com.ransombiyato.demiforge.core.recovery.BackupManager
import com.ransombiyato.demiforge.core.storage.ModStorage
import java.nio.file.Files
import java.nio.file.Path

class MainActivity : Activity() {
    private val backgroundColor = Color.rgb(16, 20, 28)
    private val surface = Color.rgb(25, 33, 45)
    private val primary = Color.rgb(93, 219, 148)
    private val textColor = Color.rgb(245, 247, 251)
    private val muted = Color.rgb(174, 184, 200)

    private lateinit var content: LinearLayout
    private lateinit var log: EventLog
    private lateinit var storage: ModStorage
    private lateinit var manager: ModManager
    private lateinit var dummyRoot: Path
    private var selectedAdapter: GameAdapter = DummyGameAdapter()
    private val importModDirectoryRequest = 4101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        setupCore()
        setContentView(buildRoot())
        showDashboard()
    }

    @Deprecated("Storage Access Framework callback retained to avoid an additional activity-result dependency")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != importModDirectoryRequest || resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            importModTree(uri)
            showMods()
        } catch (exception: Exception) {
            log.error("import", "Could not import selected mod directory: ${exception.message}")
            showLogs()
        }
    }

    private fun setupCore() {
        val stateRoot = filesDir.toPath().resolve("demiforge")
        log = EventLog()
        storage = ModStorage(stateRoot, log)
        manager = ModManager(storage, BackupManager(stateRoot.resolve("backups")), log)
        dummyRoot = filesDir.toPath().resolve("dummy-game")
        seedDummyEnvironment()
    }

    private fun buildRoot(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        root.addView(TextView(this).apply {
            text = "DEMI FORGE"
            setTextColor(primary)
            textSize = 21f
            setTypeface(null, 1)
            letterSpacing = 0.08f
        })
        root.addView(TextView(this).apply {
            text = "Safe Android mod management"
            setTextColor(textColor)
            textSize = 13f
            setPadding(0, dp(2), 0, dp(12))
        })
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(tabButton("Home") { showDashboard() }, LinearLayout.LayoutParams(0, dp(42), 1f))
            addView(tabButton("Mods") { showMods() }, LinearLayout.LayoutParams(0, dp(42), 1f))
            addView(tabButton("Settings") { showSettings() }, LinearLayout.LayoutParams(0, dp(42), 1f))
            addView(tabButton("Logs") { showLogs() }, LinearLayout.LayoutParams(0, dp(42), 1f))
        })
        root.addView(ScrollView(this).apply {
            setPadding(0, dp(12), 0, 0)
            content = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL }
            addView(content)
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        return root
    }

    private fun showDashboard() {
        content.removeAllViews()
        val inspection = inspection()
        content.addView(title("Game status"))
        content.addView(card().apply {
            addView(line("${inspection.displayName}", inspection.status.name.replace('_', ' ')))
            addView(body(inspection.message))
            addView(line("Game version", inspection.version ?: "Not detected"))
            addView(line("Installed mods", storage.discover().size.toString()))
        })
        content.addView(title("Quick actions"))
        content.addView(primaryButton("Apply enabled mods") {
            val result = manager.apply(selectedAdapter, inspection())
            log.info("ui", result.message)
            showDashboard()
        })
        content.addView(secondaryButton("Use dummy game environment") {
            selectedAdapter = DummyGameAdapter()
            log.info("ui", "Selected harmless dummy-game adapter")
            showDashboard()
        })
        content.addView(secondaryButton("Inspect Deltarune adapter") {
            selectedAdapter = DeltaruneAdapter()
            log.info("ui", "Selected Deltarune diagnostic adapter")
            showDashboard()
        })
        content.addView(body("Deltarune is intentionally diagnostic-only. DemiForge does not scan protected app storage, modify APKs, or claim a patch mechanism that Android does not expose."))
    }

    private fun showMods() {
        content.removeAllViews()
        content.addView(title("Installed mods"))
        content.addView(primaryButton("Import mod directory") { chooseModDirectory() })
        val mods = storage.discover()
        if (mods.isEmpty()) content.addView(body("No valid mods are installed. The dummy environment can be seeded again from Settings."))
        mods.forEach { mod -> content.addView(modCard(mod)) }
    }

    private fun showDetails(mod: InstalledMod) {
        content.removeAllViews()
        content.addView(title(mod.manifest.name))
        content.addView(card().apply {
            addView(line("Author", mod.manifest.author))
            addView(line("Version", mod.manifest.version))
            addView(line("Target", mod.manifest.targetGame))
            addView(line("State", if (mod.enabled) "Enabled" else "Disabled"))
            addView(body(mod.manifest.description))
            addView(line("Dependencies", mod.manifest.dependencies.joinToString { it.id }.ifBlank { "None" }))
            addView(line("Conflicts", mod.manifest.conflicts.joinToString { it.id }.ifBlank { "None" }))
            addView(line("Patches", mod.manifest.patches.size.toString()))
        })
        content.addView(primaryButton(if (mod.enabled) "Disable mod" else "Enable mod") {
            storage.setEnabled(mod.manifest.id, !mod.enabled)
            showDetails(storage.discover().first { it.manifest.id == mod.manifest.id })
        })
        content.addView(secondaryButton("Back to mods") { showMods() })
    }

    private fun showSettings() {
        content.removeAllViews()
        content.addView(title("Settings & recovery"))
        content.addView(card().apply {
            addView(line("Mod directory", filesDir.toPath().resolve("demiforge/mods").toString()))
            addView(line("Backup directory", filesDir.toPath().resolve("demiforge/backups").toString()))
            addView(line("Safe mode", if (manager.safeMode) "On" else "Off"))
        })
        content.addView(primaryButton(if (manager.safeMode) "Disable safe mode" else "Enable safe mode") {
            manager.setSafeMode(!manager.safeMode)
            showSettings()
        })
        content.addView(secondaryButton("Recreate harmless demo mods") {
            seedDummyEnvironment(force = true)
            showSettings()
        })
        content.addView(secondaryButton("Import a mod directory") { chooseModDirectory() })
        content.addView(body("Patch operations are transaction-like: copy-mode targets are backed up first, overlay-mode changes are written beneath a dedicated .demiforge-overlay layer, and failures restore the recorded backup."))
    }

    private fun showLogs() {
        content.removeAllViews()
        content.addView(title("Diagnostic log"))
        val entries = log.snapshot()
        if (entries.isEmpty()) content.addView(body("No events have been recorded yet."))
        entries.asReversed().forEach { entry ->
            content.addView(card().apply {
                addView(line("${entry.severity} · ${entry.category}", entry.timestamp.toString()))
                addView(body(entry.message))
            })
        }
        content.addView(secondaryButton("Clear logs") { log.clear(); showLogs() })
    }

    private fun modCard(mod: InstalledMod): View = card().apply {
        addView(line(mod.manifest.name, "${mod.manifest.version} · ${if (mod.enabled) "Enabled" else "Disabled"}"))
        addView(body(mod.manifest.description))
        addView(secondaryButton("View details") { showDetails(mod) })
    }

    private fun inspection(): GameInspection = when (selectedAdapter) {
        is DummyGameAdapter -> selectedAdapter.inspect(dummyRoot)
        else -> selectedAdapter.inspect(null)
    }

    private fun seedDummyEnvironment(force: Boolean = false) {
        Files.createDirectories(dummyRoot.resolve("data"))
        writeText(dummyRoot.resolve("dummy-game.json"), "{\"version\":\"1.0.0\"}")
        writeText(dummyRoot.resolve("data/settings.txt"), "difficulty=normal\n")
        val source = filesDir.toPath().resolve("example-mod-source")
        if (force) com.ransombiyato.demiforge.core.storage.FileTree.delete(source)
        if (!Files.exists(source)) {
            Files.createDirectories(source.resolve("payload"))
            writeText(source.resolve("manifest.json"), """{
              \"schemaVersion\": 1,
              \"id\": \"sample-overlay\",
              \"name\": \"Sample Overlay\",
              \"author\": \"DemiForge\",
              \"version\": \"1.0.0\",
              \"description\": \"A harmless test mod that writes an overlay file in the dummy environment.\",
              \"targetGame\": \"dummy-game\",
              \"supportedGameVersions\": [\"1.0.0\"],
              \"patches\": [{\"source\": \"payload/greeting.txt\", \"target\": \"content/greeting.txt\", \"mode\": \"OVERLAY\"}]
            }""".trimIndent())
            writeText(source.resolve("payload/greeting.txt"), "Hello from the harmless DemiForge dummy mod.\n")
            storage.install(source)
            storage.setEnabled("sample-overlay", true)
            log.info("demo", "Created and installed the harmless Sample Overlay dummy mod")
        }
    }

    private fun chooseModDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, importModDirectoryRequest)
    }

    private fun importModTree(uri: Uri) {
        val root = requireNotNull(DocumentFile.fromTreeUri(this, uri)) { "Selected directory is unavailable" }
        val staging = cacheDir.toPath().resolve("import-${System.currentTimeMillis()}")
        copyDocumentTree(root, staging)
        val result = storage.install(staging)
        com.ransombiyato.demiforge.core.storage.FileTree.delete(staging)
        if (!result.valid) throw IllegalArgumentException(result.issues.joinToString { it.message })
        log.info("import", "Imported ${requireNotNull(result.manifest).id} from a user-selected directory")
    }

    private fun copyDocumentTree(source: DocumentFile, destination: Path) {
        if (source.isDirectory) {
            Files.createDirectories(destination)
            source.listFiles().forEach { child -> copyDocumentTree(child, destination.resolve(child.name ?: "unnamed")) }
        } else {
            Files.createDirectories(destination.parent)
            contentResolver.openInputStream(source.uri).use { input ->
                requireNotNull(input) { "Cannot read ${source.name}" }
                Files.newOutputStream(destination).use { output -> input.copyTo(output) }
            }
        }
    }

    private fun title(value: String) = TextView(this).apply { text = value; textSize = 19f; setTextColor(textColor); setTypeface(null, 1); setPadding(0, dp(8), 0, dp(8)) }
    private fun body(value: String) = TextView(this).apply { text = value; textSize = 14f; setTextColor(muted); setLineSpacing(dp(3).toFloat(), 1f); setPadding(0, dp(4), 0, dp(4)) }
    private fun line(left: String, right: String) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(4), 0, dp(4))
        addView(TextView(this@MainActivity).apply { text = left; textSize = 14f; setTextColor(textColor) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        addView(TextView(this@MainActivity).apply { text = right; textSize = 12f; setTextColor(muted); gravity = Gravity.END }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    }
    private fun card() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(surface); setPadding(dp(14), dp(10), dp(14), dp(10)); val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); params.bottomMargin = dp(10); layoutParams = params }
    private fun tabButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; textSize = 11f; setTextColor(textColor); setOnClickListener { listener() } }
    private fun primaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(Color.BLACK); setBackgroundColor(primary); setOnClickListener { listener() }; setPadding(0, dp(3), 0, dp(3)) }
    private fun secondaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(textColor); setOnClickListener { listener() } }
    private fun writeText(path: Path, value: String) {
        Files.newOutputStream(path).use { output -> output.write(value.toByteArray(Charsets.UTF_8)) }
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
