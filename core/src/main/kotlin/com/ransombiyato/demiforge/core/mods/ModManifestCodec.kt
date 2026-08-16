package com.ransombiyato.demiforge.core.mods

import com.ransombiyato.demiforge.core.model.ModConflict
import com.ransombiyato.demiforge.core.model.ModDependency
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.PatchMode
import com.ransombiyato.demiforge.core.model.PatchOperation
import org.json.JSONArray
import org.json.JSONObject

object ModManifestCodec {
    fun parse(text: String): ModManifest {
        val json = JSONObject(text)
        return ModManifest(
            schemaVersion = json.optInt("schemaVersion", 1),
            id = json.requiredString("id"),
            name = json.requiredString("name"),
            author = json.requiredString("author"),
            version = json.requiredString("version"),
            description = json.optString("description", ""),
            targetGame = json.requiredString("targetGame"),
            supportedGameVersions = json.stringList("supportedGameVersions", listOf("*")),
            dependencies = json.optJSONArray("dependencies").objects { item ->
                ModDependency(
                    id = item.requiredString("id"),
                    minVersion = item.optString("minVersion").ifBlank { null },
                    required = item.optBoolean("required", true),
                )
            },
            conflicts = json.optJSONArray("conflicts").objects { item ->
                ModConflict(item.requiredString("id"), item.optString("reason", ""))
            },
            loadAfter = json.stringList("loadAfter"),
            loadBefore = json.stringList("loadBefore"),
            patches = json.optJSONArray("patches").objects { item ->
                PatchOperation(
                    source = item.requiredString("source"),
                    target = item.requiredString("target"),
                    mode = PatchMode.valueOf(item.optString("mode", PatchMode.OVERLAY.name).uppercase()),
                )
            },
            configuration = json.optJSONObject("configuration")?.let { config ->
                config.keys().asSequence().associateWith { key -> config.opt(key).toString() }
            }.orEmpty(),
        )
    }

    fun encode(manifest: ModManifest): String = JSONObject().apply {
        put("schemaVersion", manifest.schemaVersion)
        put("id", manifest.id)
        put("name", manifest.name)
        put("author", manifest.author)
        put("version", manifest.version)
        put("description", manifest.description)
        put("targetGame", manifest.targetGame)
        put("supportedGameVersions", JSONArray(manifest.supportedGameVersions))
        put("dependencies", JSONArray(manifest.dependencies.map { dependency -> JSONObject().apply {
            put("id", dependency.id); dependency.minVersion?.let { put("minVersion", it) }; put("required", dependency.required)
        } }))
        put("conflicts", JSONArray(manifest.conflicts.map { conflict -> JSONObject().apply {
            put("id", conflict.id); put("reason", conflict.reason)
        } }))
        put("loadAfter", JSONArray(manifest.loadAfter))
        put("loadBefore", JSONArray(manifest.loadBefore))
        put("patches", JSONArray(manifest.patches.map { patch -> JSONObject().apply {
            put("source", patch.source); put("target", patch.target); put("mode", patch.mode.name)
        } }))
        put("configuration", JSONObject(manifest.configuration))
    }.toString(2)

    private fun JSONObject.requiredString(key: String): String = optString(key).trim().also {
        require(it.isNotEmpty()) { "Manifest field '$key' is required" }
    }

    private fun JSONObject.stringList(key: String, default: List<String> = emptyList()): List<String> =
        optJSONArray(key)?.let { array -> (0 until array.length()).map { index -> array.getString(index) } } ?: default

    private fun <T> JSONArray?.objects(mapper: (JSONObject) -> T): List<T> =
        if (this == null) emptyList() else (0 until length()).map { index -> mapper(getJSONObject(index)) }
}
