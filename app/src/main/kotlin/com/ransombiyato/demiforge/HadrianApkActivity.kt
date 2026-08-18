package com.ransombiyato.demiforge

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.ransombiyato.demiforge.core.gamemaker.GameMakerResourceCategory
import com.ransombiyato.demiforge.core.gamemaker.GameMakerObjectSpriteAlias
import com.ransombiyato.demiforge.core.gamemaker.GameMakerStringEdit
import java.nio.file.Files
import java.nio.file.Path

/**
 * Directly patches a user-selected copy of Hadrian's APK. No Gradle build or game recompilation is
 * performed on the phone: only selected assets are swapped, the APK archive is rebuilt, and the
 * result is signed and verified before the standard Android installer is opened.
 */
class HadrianApkActivity : Activity() {
    private val backgroundColor = Color.rgb(16, 20, 28)
    private val surface = Color.rgb(25, 33, 45)
    private val primary = Color.rgb(93, 219, 148)
    private val textColor = Color.rgb(245, 247, 251)
    private val muted = Color.rgb(174, 184, 200)
    private val warning = Color.rgb(255, 202, 92)

    private lateinit var content: LinearLayout
    private lateinit var patcher: HadrianApkPatchService
    private var selection: HadrianApkSelection? = null
    private var pendingTarget: String? = null
    private var patched: HadrianPatchedApk? = null
    private var inspection: HadrianGameMakerInspection? = null
    private var resourceSearch: HadrianResourceSearch? = null
    private val replacements = linkedMapOf<String, Path>()
    private val stringEdits = linkedMapOf<String, LinkedHashMap<Int, String>>()
    private val spriteAliases = linkedMapOf<String, LinkedHashMap<Int, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        patcher = HadrianApkPatchService(this)
        setContentView(buildRoot())
        render()
    }

    @Deprecated("Activity result API is sufficient for this small native picker workflow")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            when (requestCode) {
                PICK_APK -> {
                    selection = patcher.prepare(uri)
                    replacements.clear()
                    patched = null
                    inspection = null
                    resourceSearch = null
                    stringEdits.clear()
                    spriteAliases.clear()
                }
                PICK_REPLACEMENT -> {
                    val target = requireNotNull(pendingTarget) { "No APK target was selected." }
                    replacements[target] = copyToCache(uri, "replacement-${System.currentTimeMillis()}")
                    pendingTarget = null
                    patched = null
                }
            }
            render()
        } catch (exception: Exception) {
            showError(exception.message ?: "Could not use that file.")
        }
    }

    private fun buildRoot(): View = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundColor(backgroundColor)
        setPadding(dp(16), dp(12), dp(16), dp(12))
        addView(TextView(this@HadrianApkActivity).apply {
            this.text = "DEMI FORGE · GAME DATA EDITOR"
            setTextColor(primary)
            textSize = 19f
            setTypeface(null, 1)
        })
        addView(TextView(this@HadrianApkActivity).apply {
            text = "Hadrian Deltarune port · GameMaker resource inspector"
            setTextColor(textColor)
            textSize = 13f
            setPadding(0, dp(2), 0, dp(10))
        })
        addView(ScrollView(this@HadrianApkActivity).apply {
            content = LinearLayout(this@HadrianApkActivity).apply { orientation = LinearLayout.VERTICAL }
            addView(content)
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
    }

    private fun render() {
        content.removeAllViews()
        content.addView(body("Select the original Hadrian APK—not an unzipped folder. DemiForge makes an app-private backup, swaps only selected assets, rebuilds the archive, signs it, verifies the signature, and opens Android’s installer."))
        val current = selection
        if (current == null) {
            content.addView(primaryButton("Select original Hadrian APK") { chooseApk() })
            content.addView(warningCard("A modded APK has a different signature. Back up saves first; Android may require uninstalling the original port before it installs the modded copy."))
            return
        }
        content.addView(card().apply {
            addView(line("Original APK backup", formatBytes(current.sizeBytes)))
            addView(line("Modifiable asset payloads", current.payloads.size.toString()))
        })
        current.payloads.forEach { payload ->
            val replacement = replacements[payload.path]
            content.addView(card().apply {
                addView(line(payload.path, if (replacement == null) "Original" else "Replacement ready"))
                addView(body("${formatBytes(payload.sizeBytes)} · ${replacement?.fileName?.toString() ?: "No replacement selected"}"))
                if (payload.path.lowercase().endsWith(".droid")) {
                    addView(secondaryButton("Inspect GameMaker resources") { inspectPayload(payload.path) })
                }
                addView(secondaryButton("Choose replacement") { chooseReplacement(payload.path) })
            })
        }
        inspection?.let { result ->
            content.addView(card().apply {
                addView(line("Resource inspection", result.targetPath))
                addView(body("Read-only first pass. DemiForge parsed this GameMaker FORM payload directly; these are actual resource chunks, not guessed files."))
                addView(line("Chunks", result.chunks.size.toString()))
                addView(line("Strings", result.stringCount.toString()))
            })
            result.chunks.groupBy { it.category }.toSortedMap(compareBy { it.label }).forEach { (category, chunks) ->
                content.addView(card().apply {
                    addView(line(category.label, chunks.joinToString { it.name }))
                    addView(body(chunks.joinToString { "${it.name} · ${formatBytes(it.payloadSize)}" }))
                })
            }
            if (result.stringPreview.isNotEmpty()) {
                content.addView(body("String preview (first ${result.stringPreview.size} entries)"))
                result.stringPreview.forEach { string ->
                    content.addView(card().apply {
                        addView(line("#${string.index}", "byte ${string.offset}"))
                        val draft = stringEdits[result.targetPath]?.get(string.index)
                        addView(body((draft ?: string.content).take(600)))
                        addView(secondaryButton(if (draft == null) "Edit this string" else "Edit draft string") {
                            editString(result, string.index, draft ?: string.content)
                        })
                    })
                }
            }
            if (result.namedResources.isNotEmpty()) {
                content.addView(body("Named GameMaker resources (first 40 per chunk)"))
                result.namedResources.forEach { (chunk, resources) ->
                    content.addView(card().apply {
                        addView(line(chunk, "${resources.size} indexed"))
                        addView(body(resources.joinToString("\n") { resource -> "#${resource.index}  ${resource.name ?: "<unnamed>"}" }))
                    })
                }
            }
            if (result.resourcePreviewErrors.isNotEmpty()) {
                content.addView(warningCard(result.resourcePreviewErrors.entries.joinToString("\n") { (chunk, error) -> "$chunk: $error" }))
            }
            content.addView(secondaryButton("Search all named resources") { searchResources(result.targetPath) })
            resourceSearch?.takeIf { it.targetPath == result.targetPath }?.let { search ->
                content.addView(card().apply {
                    addView(line("Search: ${search.query}", "${search.matches.size} match(es)"))
                    addView(body(search.matches.joinToString("\n") { match -> "${match.chunkName} #${match.index}  ${match.name ?: "<unnamed>"}" }))
                })
            }
            val edits = stringEdits[result.targetPath].orEmpty()
            val aliases = spriteAliases[result.targetPath].orEmpty()
            content.addView(secondaryButton("Alias object to an existing sprite") { configureObjectSpriteAlias(result.targetPath) })
            if (aliases.isNotEmpty()) {
                content.addView(card().apply {
                    addView(line("Visual aliases", "${aliases.size} draft(s)"))
                    addView(body(aliases.entries.joinToString("\n") { (objectId, spriteId) -> "OBJT #$objectId → SPRT #$spriteId" }))
                })
            }
            if (edits.isNotEmpty() || aliases.isNotEmpty()) {
                content.addView(primaryButton("Prepare ${edits.size} text and ${aliases.size} visual edit(s) for APK") { prepareEditorDraft(result.targetPath, edits, aliases) })
                content.addView(warningCard("String edits are deliberately limited to the original UTF-8 byte length. This preserves every existing GameMaker offset; longer text requires a complete version-aware serializer."))
            }
        }
        if (replacements.isNotEmpty()) {
            content.addView(primaryButton("Build and sign modded APK (${replacements.size} file(s))") { buildPatched() })
        } else {
            content.addView(warningCard("Choose an Android-ready replacement payload before building. The port’s `assets/` files shown above are the only permitted targets."))
        }
        patched?.let { output ->
            content.addView(card().apply {
                addView(line("Signature verified", listOfNotNull(if (output.verifiedV1) "v1" else null, if (output.verifiedV2) "v2" else null, if (output.verifiedV3) "v3" else null).joinToString(", ")))
                addView(body("${output.replacementCount} asset replacement(s) are ready in a separate signed APK."))
            })
            content.addView(primaryButton("Open Android installer") { install(output) })
        }
        content.addView(secondaryButton("Choose a different original APK") {
            selection = null
            replacements.clear()
            patched = null
            inspection = null
            resourceSearch = null
            stringEdits.clear()
            spriteAliases.clear()
            render()
        })
    }

    private fun chooseApk() {
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/vnd.android.package-archive"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, PICK_APK)
    }

    private fun chooseReplacement(target: String) {
        pendingTarget = target
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, PICK_REPLACEMENT)
    }

    private fun buildPatched() {
        try {
            patched = patcher.patch(requireNotNull(selection), replacements.toMap())
            render()
        } catch (exception: Exception) {
            showError("The original backup was preserved. ${exception.message}")
        }
    }

    private fun inspectPayload(targetPath: String) {
        try {
            inspection = patcher.inspectGameMakerPayload(requireNotNull(selection), targetPath)
            resourceSearch = null
            render()
        } catch (exception: Exception) {
            showError("Could not parse $targetPath as GameMaker data: ${exception.message}")
        }
    }

    private fun editString(result: HadrianGameMakerInspection, index: Int, original: String) {
        val input = EditText(this).apply {
            setText(original)
            setSelectAllOnFocus(false)
            minLines = 3
            maxLines = 10
        }
        AlertDialog.Builder(this)
            .setTitle("Edit GameMaker string #$index")
            .setMessage("The replacement must not use more UTF-8 bytes than the original. This keeps the payload’s internal offsets unchanged.")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save draft") { _, _ ->
                stringEdits.getOrPut(result.targetPath) { linkedMapOf() }[index] = input.text.toString()
                patched = null
                render()
            }
            .show()
    }

    private fun prepareEditorDraft(targetPath: String, edits: Map<Int, String>, aliases: Map<Int, Int>) {
        try {
            val draft = patcher.createEditorDraft(
                requireNotNull(selection),
                targetPath,
                edits.map { (index, replacement) -> GameMakerStringEdit(index, replacement) },
                aliases.map { (objectIndex, spriteIndex) -> GameMakerObjectSpriteAlias(objectIndex, spriteIndex) },
            )
            replacements[targetPath] = draft
            patched = null
            render()
        } catch (exception: Exception) {
            showError("The original APK was not changed. ${exception.message}")
        }
    }

    private fun configureObjectSpriteAlias(targetPath: String) {
        val objectInput = EditText(this).apply { hint = "Object index (OBJT #)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val spriteInput = EditText(this).apply { hint = "Sprite index (SPRT #)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), 0, dp(18), 0)
            addView(body("Use Search all named resources to find the actual `OBJT` and `SPRT` indices. This changes only the object’s existing 32-bit sprite reference; it does not touch texture pages or other party members."))
            addView(objectInput)
            addView(spriteInput)
        }
        AlertDialog.Builder(this)
            .setTitle("Alias object to existing sprite")
            .setView(form)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save visual draft") { _, _ ->
                try {
                    val objectIndex = objectInput.text.toString().toInt()
                    val spriteIndex = spriteInput.text.toString().toInt()
                    require(objectIndex >= 0 && spriteIndex >= 0) { "Resource indices cannot be negative." }
                    spriteAliases.getOrPut(targetPath) { linkedMapOf() }[objectIndex] = spriteIndex
                    patched = null
                    render()
                } catch (exception: Exception) {
                    showError(exception.message ?: "Enter valid OBJT and SPRT indices.")
                }
            }
            .show()
    }

    private fun searchResources(targetPath: String) {
        val input = EditText(this).apply { hint = "kris, flowey, battle, act…"; maxLines = 1 }
        AlertDialog.Builder(this)
            .setTitle("Search named GameMaker resources")
            .setMessage("Searches actual sprite, object, room, script, code, and related resource names in this payload.")
            .setView(input)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Search") { _, _ ->
                try {
                    resourceSearch = patcher.searchNamedResources(requireNotNull(selection), targetPath, input.text.toString())
                    render()
                } catch (exception: Exception) {
                    showError(exception.message ?: "Could not search the GameMaker resources.")
                }
            }
            .show()
    }

    private fun install(output: HadrianPatchedApk) {
        val uri = FileProvider.getUriForFile(this, "$packageName.files", output.apk.toFile())
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        })
    }

    private fun copyToCache(uri: Uri, name: String): Path {
        val destination = cacheDir.toPath().resolve(name)
        contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Cannot read the selected replacement." }
            Files.newOutputStream(destination).use { output -> input.copyTo(output) }
        }
        return destination
    }

    private fun showError(message: String) {
        content.removeAllViews()
        content.addView(warningCard(message))
        content.addView(secondaryButton("Back") { render() })
    }

    private fun body(value: String) = TextView(this).apply { text = value; textSize = 14f; setTextColor(muted); setLineSpacing(dp(3).toFloat(), 1f); setPadding(0, dp(4), 0, dp(8)) }
    private fun line(left: String, right: String) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(4), 0, dp(4))
        addView(TextView(this@HadrianApkActivity).apply { text = left; textSize = 14f; setTextColor(textColor) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        addView(TextView(this@HadrianApkActivity).apply { text = right; textSize = 12f; setTextColor(muted); gravity = Gravity.END }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    }
    private fun card() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(surface); setPadding(dp(14), dp(10), dp(14), dp(10)); layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply { bottomMargin = dp(10) } }
    private fun warningCard(value: String) = card().apply { setBackgroundColor(Color.rgb(73, 57, 32)); addView(TextView(this@HadrianApkActivity).apply { text = value; textSize = 14f; setTextColor(warning); setPadding(0, dp(3), 0, dp(3)) }) }
    private fun primaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(Color.BLACK); setBackgroundColor(primary); setOnClickListener { listener() }; setPadding(0, dp(3), 0, dp(3)) }
    private fun secondaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(textColor); setOnClickListener { listener() } }
    private fun formatBytes(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KiB"
        else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MiB"
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val PICK_APK = 9001
        const val PICK_REPLACEMENT = 9002
    }
}
