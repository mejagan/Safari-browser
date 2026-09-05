package com.example.engine

import com.example.model.ContentScriptRule
import com.example.model.Extension
import com.example.model.ExtensionManifest
import com.example.model.PrivacyReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ManifestV3ExtensionManager {

    private val _extensions = MutableStateFlow<List<Extension>>(emptyList())
    val extensions: StateFlow<List<Extension>> = _extensions.asStateFlow()

    private val _privacyReport = MutableStateFlow(
        PrivacyReport(
            totalTrackersBlocked = 0,
            totalAdsBlocked = 0,
            httpsUpgraded = true,
            thirdPartyCookiesBlocked = true,
            fingerprintingShieldActive = true
        )
    )
    val privacyReport: StateFlow<PrivacyReport> = _privacyReport.asStateFlow()

    init {
        // Register built-in Manifest V3 extensions
        val liquidGuardManifest = ExtensionManifest(
            manifestVersion = 3,
            id = "liquid-guard-mv3",
            name = "Liquid Guard MV3",
            version = "3.2.0",
            description = "High-performance Manifest V3 ad-shield, anti-tracker filter, and cosmetic cleaner.",
            permissions = listOf("declarativeNetRequest", "scripting", "tabs", "storage"),
            hostPermissions = listOf("<all_urls>"),
            contentScripts = listOf(
                ContentScriptRule(
                    matches = listOf("http://*/*", "https://*/*"),
                    css = listOf(AdBlockEngine.COSMETIC_AD_BLOCK_CSS),
                    js = listOf(AdBlockEngine.COSMETIC_INJECTION_JS),
                    runAt = "document_idle"
                )
            )
        )

        val darkReaderManifest = ExtensionManifest(
            manifestVersion = 3,
            id = "liquid-dark-mode-mv3",
            name = "Liquid Dark Mode MV3",
            version = "1.1.0",
            description = "Intelligent client-side high-contrast dark palette injection for eye care.",
            permissions = listOf("scripting", "tabs"),
            hostPermissions = listOf("<all_urls>")
        )

        _extensions.value = listOf(
            Extension(manifest = liquidGuardManifest, isEnabled = true, blockedCount = 0, isBuiltIn = true),
            Extension(manifest = darkReaderManifest, isEnabled = false, blockedCount = 0, isBuiltIn = true)
        )
    }

    fun isAdBlockerActive(): Boolean {
        return _extensions.value.find { it.manifest.id == "liquid-guard-mv3" }?.isEnabled ?: true
    }

    fun toggleExtension(extensionId: String) {
        _extensions.update { list ->
            list.map { ext ->
                if (ext.manifest.id == extensionId) {
                    ext.copy(isEnabled = !ext.isEnabled)
                } else {
                    ext
                }
            }
        }
    }

    fun recordBlock(isTracker: Boolean = true) {
        _extensions.update { list ->
            list.map { ext ->
                if (ext.manifest.id == "liquid-guard-mv3") {
                    ext.copy(blockedCount = ext.blockedCount + 1)
                } else {
                    ext
                }
            }
        }
        _privacyReport.update { report ->
            if (isTracker) {
                report.copy(totalTrackersBlocked = report.totalTrackersBlocked + 1)
            } else {
                report.copy(totalAdsBlocked = report.totalAdsBlocked + 1)
            }
        }
    }

    fun getActiveContentScripts(): List<String> {
        val scripts = mutableListOf<String>()
        val guard = _extensions.value.find { it.manifest.id == "liquid-guard-mv3" }
        if (guard?.isEnabled == true) {
            scripts.add(AdBlockEngine.COSMETIC_INJECTION_JS)
        }
        return scripts
    }
}
