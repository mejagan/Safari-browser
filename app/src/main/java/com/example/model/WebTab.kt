package com.example.model

import java.util.UUID

enum class TabCategory(val label: String) {
    ALL("All"),
    SHOPPING("Shopping"),
    ARTICLES("Articles"),
    SOCIAL("Social"),
    TECH("Tech")
}

enum class WallpaperTheme(val displayName: String) {
    ICE_BLUE("Ice Blue"),
    DUSK_SAND("Dusk Sand"),
    PINE_GREEN("Pine Green"),
    DEEP_DARK("Deep Dark")
}

enum class SearchEngine(val displayName: String, val searchUrlPrefix: String) {
    GOOGLE("Google", "https://www.google.com/search?q="),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q="),
    BING("Bing", "https://www.bing.com/search?q="),
    BRAVE("Brave", "https://search.brave.com/search?q="),
    ECOSIA("Ecosia", "https://www.ecosia.org/search?q=")
}

data class WebTab(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Tab",
    val url: String = "liquid://newtab",
    val displayDomain: String = "New Tab",
    val category: TabCategory = TabCategory.ALL,
    val isPrivate: Boolean = false,
    val previewGradient: String = "ice-blue",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isDesktopMode: Boolean = false,
    val groupId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class TabGroup(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconType: String = "stack", // "phone", "bag", "pot", "code", "star", "stack"
    val count: Int = 0,
    val category: TabCategory = TabCategory.ALL,
    val isSystemDefault: Boolean = false
)

data class QuickShortcut(
    val title: String,
    val url: String,
    val domain: String,
    val category: String,
    val iconName: String
)
