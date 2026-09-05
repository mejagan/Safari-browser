package com.example.model

data class ExtensionManifest(
    val manifestVersion: Int = 3,
    val id: String,
    val name: String,
    val version: String,
    val description: String,
    val author: String = "Liquid Browser Team",
    val permissions: List<String> = emptyList(),
    val hostPermissions: List<String> = emptyList(),
    val contentScripts: List<ContentScriptRule> = emptyList()
)

data class ContentScriptRule(
    val matches: List<String>,
    val css: List<String> = emptyList(),
    val js: List<String> = emptyList(),
    val runAt: String = "document_idle"
)

data class Extension(
    val manifest: ExtensionManifest,
    val isEnabled: Boolean = true,
    val blockedCount: Int = 0,
    val isBuiltIn: Boolean = true
)

data class PrivacyReport(
    val totalTrackersBlocked: Int = 0,
    val totalAdsBlocked: Int = 0,
    val httpsUpgraded: Boolean = true,
    val thirdPartyCookiesBlocked: Boolean = true,
    val fingerprintingShieldActive: Boolean = true
)
