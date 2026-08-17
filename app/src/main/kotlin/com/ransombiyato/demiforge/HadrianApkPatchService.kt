package com.ransombiyato.demiforge

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import com.ransombiyato.demiforge.core.gamemaker.GameMakerChunk
import com.ransombiyato.demiforge.core.gamemaker.GameMakerFormInspector
import com.ransombiyato.demiforge.core.gamemaker.GameMakerStringEntry
import com.ransombiyato.demiforge.core.storage.ApkArchive
import com.ransombiyato.demiforge.core.storage.ApkPayloadEntry
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.Instant
import java.util.zip.ZipFile

data class HadrianApkSelection(
    val id: String,
    val originalBackup: Path,
    val payloads: List<ApkPayloadEntry>,
    val sizeBytes: Long,
)

data class HadrianPatchedApk(
    val apk: Path,
    val originalBackup: Path,
    val replacementCount: Int,
    val verifiedV1: Boolean,
    val verifiedV2: Boolean,
    val verifiedV3: Boolean,
)

data class HadrianGameMakerInspection(
    val targetPath: String,
    val chunks: List<GameMakerChunk>,
    val stringCount: Int,
    val stringPreview: List<GameMakerStringEntry>,
)

/**
 * Rebuilds a user-owned APK in app-private storage. The result is re-signed with DemiForge's
 * Android Keystore key, which is why Android treats it as a separately signed install.
 */
class HadrianApkPatchService(private val context: Context) {
    private val resolver = context.contentResolver
    private val root: Path = context.filesDir.toPath().resolve("demiforge/hadrian-apk")

    fun prepare(sourceUri: Uri): HadrianApkSelection {
        Files.createDirectories(root.resolve("originals"))
        val id = "hadrian-${System.currentTimeMillis()}"
        val backup = root.resolve("originals/$id-original.apk")
        resolver.openInputStream(sourceUri).use { input ->
            requireNotNull(input) { "Cannot read the selected APK." }
            Files.newOutputStream(backup).use { output -> input.copyTo(output) }
        }
        return try {
            val payloads = ApkArchive.listPayloads(backup)
            require(payloads.isNotEmpty()) { "The APK contains no game.droid, data.droid, or WAD payload under assets/." }
            HadrianApkSelection(id, backup, payloads, Files.size(backup))
        } catch (exception: Exception) {
            Files.deleteIfExists(backup)
            throw exception
        }
    }

    fun patch(selection: HadrianApkSelection, replacements: Map<String, Path>): HadrianPatchedApk {
        require(Files.isRegularFile(selection.originalBackup)) { "Original APK backup is no longer available." }
        Files.createDirectories(root.resolve("outputs"))
        val unsigned = root.resolve("outputs/${selection.id}-unsigned.apk")
        val signed = root.resolve("outputs/${selection.id}-flowey-modded.apk")
        Files.deleteIfExists(unsigned)
        Files.deleteIfExists(signed)
        try {
            ApkArchive.rebuildWithReplacements(selection.originalBackup, unsigned, replacements)
            val signer = ApkSigner.SignerConfig.Builder("DemiForge", signingKey(), listOf(signingCertificate())).build()
            ApkSigner.Builder(listOf(signer))
                .setInputApk(unsigned.toFile())
                .setOutputApk(signed.toFile())
                .setMinSdkVersion(26)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .setV3SigningEnabled(true)
                .setV4SigningEnabled(false)
                .setCreatedBy("DemiForge")
                .build()
                .sign()
            val verification = ApkVerifier.Builder(signed.toFile()).build().verify()
            require(verification.isVerified) { "The rebuilt APK signature did not verify." }
            return HadrianPatchedApk(
                apk = signed,
                originalBackup = selection.originalBackup,
                replacementCount = replacements.size,
                verifiedV1 = verification.isVerifiedUsingV1Scheme,
                verifiedV2 = verification.isVerifiedUsingV2Scheme,
                verifiedV3 = verification.isVerifiedUsingV3Scheme,
            )
        } catch (exception: Exception) {
            Files.deleteIfExists(signed)
            throw exception
        } finally {
            Files.deleteIfExists(unsigned)
        }
    }

    fun inspectGameMakerPayload(selection: HadrianApkSelection, targetPath: String, previewLimit: Int = 24): HadrianGameMakerInspection {
        require(previewLimit in 1..100) { "String preview limit must be between 1 and 100." }
        require(selection.payloads.any { it.path == targetPath }) { "Payload is not part of the selected APK." }
        require(targetPath.lowercase().endsWith(".droid")) { "Only GameMaker .droid payloads can be structurally inspected." }
        val extracted = root.resolve("inspection/${selection.id}-${targetPath.substringAfterLast('/')}")
        Files.createDirectories(extracted.parent)
        ZipFile(selection.originalBackup.toFile()).use { archive ->
            val entry = requireNotNull(archive.getEntry(targetPath)) { "APK no longer contains $targetPath." }
            require(!entry.isDirectory) { "Selected payload is a directory." }
            archive.getInputStream(entry).use { input ->
                Files.newOutputStream(extracted).use { output -> input.copyTo(output) }
            }
        }
        val index = GameMakerFormInspector.inspect(extracted)
        val hasStrings = index.chunks.any { it.name == "STRG" }
        return HadrianGameMakerInspection(
            targetPath = targetPath,
            chunks = index.chunks,
            stringCount = if (hasStrings) GameMakerFormInspector.stringCount(extracted, index) else 0,
            stringPreview = if (hasStrings) GameMakerFormInspector.readStrings(extracted, index, previewLimit) else emptyList(),
        )
    }

    private fun signingKey(): PrivateKey = (keyStore().getKey(KEY_ALIAS, null) as? PrivateKey)
        ?: throw IllegalStateException("DemiForge signing key is unavailable.")

    private fun signingCertificate(): X509Certificate = (keyStore().getCertificate(KEY_ALIAS) as? X509Certificate)
        ?: throw IllegalStateException("DemiForge signing certificate is unavailable.")

    private fun keyStore(): KeyStore {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!store.containsAlias(KEY_ALIAS)) {
            val generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
            generator.initialize(
                KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                    .setKeySize(2048)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setCertificateNotBefore(java.util.Date.from(Instant.parse("2026-01-01T00:00:00Z")))
                    .setCertificateNotAfter(java.util.Date.from(Instant.parse("2046-01-01T00:00:00Z")))
                    .setCertificateSubject(javax.security.auth.x500.X500Principal("CN=DemiForge APK Modding"))
                    .build(),
            )
            generator.generateKeyPair()
        }
        return store
    }

    companion object {
        private const val KEY_ALIAS = "demiforge-hadrian-apk-signer-v1"
    }
}
