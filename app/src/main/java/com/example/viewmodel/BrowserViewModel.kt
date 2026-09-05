package com.example.viewmodel

import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.BookmarkEntity
import com.example.data.local.DownloadEntity
import com.example.data.local.HistoryEntity
import com.example.data.repository.BrowserRepository
import com.example.engine.ManifestV3ExtensionManager
import com.example.model.Extension
import com.example.model.PrivacyReport
import com.example.model.QuickShortcut
import com.example.model.SearchEngine
import com.example.model.TabCategory
import com.example.model.TabGroup
import com.example.model.WallpaperTheme
import com.example.model.WebTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BrowserActiveOverlay {
    NONE,
    SEARCH,
    ACTIONS,
    TABS_OVERVIEW,
    TAB_GROUPS,
    BOOKMARKS,
    HISTORY,
    DOWNLOADS,
    EXTENSIONS,
    SETTINGS
}

class BrowserViewModel(
    private val repository: BrowserRepository,
    val extensionManager: ManifestV3ExtensionManager
) : ViewModel() {

    // --- Tabs State ---
    private val defaultRegularTab = WebTab(
        title = "New Tab",
        url = "liquid://newtab",
        displayDomain = "New Tab",
        isPrivate = false,
        previewGradient = "ice-blue"
    )

    private val _regularTabs = MutableStateFlow<List<WebTab>>(listOf(defaultRegularTab))
    val regularTabs: StateFlow<List<WebTab>> = _regularTabs.asStateFlow()

    private val _privateTabs = MutableStateFlow<List<WebTab>>(emptyList())
    val privateTabs: StateFlow<List<WebTab>> = _privateTabs.asStateFlow()

    private val _activeTabId = MutableStateFlow(defaultRegularTab.id)
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _isPrivateModeActive = MutableStateFlow(false)
    val isPrivateModeActive: StateFlow<Boolean> = _isPrivateModeActive.asStateFlow()

    // --- Tab Groups ---
    private val _tabGroups = MutableStateFlow(
        listOf(
            TabGroup(name = "Personal", iconType = "phone", count = 1, isSystemDefault = true),
            TabGroup(name = "Research", iconType = "stack", count = 0),
            TabGroup(name = "Work", iconType = "bag", count = 0),
            TabGroup(name = "Favorites", iconType = "star", count = 0)
        )
    )
    val tabGroups: StateFlow<List<TabGroup>> = _tabGroups.asStateFlow()

    private val _activeGroupId = MutableStateFlow(_tabGroups.value.first().id)
    val activeGroupId: StateFlow<String> = _activeGroupId.asStateFlow()

    // --- UI Navigation Overlays ---
    private val _activeOverlay = MutableStateFlow(BrowserActiveOverlay.NONE)
    val activeOverlay: StateFlow<BrowserActiveOverlay> = _activeOverlay.asStateFlow()

    // --- Preferences ---
    private val _wallpaper = MutableStateFlow(WallpaperTheme.ICE_BLUE)
    val wallpaper: StateFlow<WallpaperTheme> = _wallpaper.asStateFlow()

    private val _searchEngine = MutableStateFlow(SearchEngine.GOOGLE)
    val searchEngine: StateFlow<SearchEngine> = _searchEngine.asStateFlow()

    private val _isJavaScriptEnabled = MutableStateFlow(true)
    val isJavaScriptEnabled: StateFlow<Boolean> = _isJavaScriptEnabled.asStateFlow()

    private val _recentlyClosedTabs = MutableStateFlow<List<WebTab>>(emptyList())
    val recentlyClosedTabs: StateFlow<List<WebTab>> = _recentlyClosedTabs.asStateFlow()

    // --- Repository Flows ---
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.bookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val history: StateFlow<List<HistoryEntity>> = repository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadEntity>> = repository.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val extensions: StateFlow<List<Extension>> = extensionManager.extensions
    val privacyReport: StateFlow<PrivacyReport> = extensionManager.privacyReport

    // --- Quick Shortcuts for New Tab ---
    val quickShortcuts = listOf(
        QuickShortcut("Google", "https://www.google.com", "google.com", "Search", "search"),
        QuickShortcut("Wikipedia", "https://www.wikipedia.org", "wikipedia.org", "Knowledge", "menu_book"),
        QuickShortcut("GitHub", "https://github.com", "github.com", "Dev", "code"),
        QuickShortcut("Reddit", "https://www.reddit.com", "reddit.com", "Community", "forum"),
        QuickShortcut("Hacker News", "https://news.ycombinator.com", "ycombinator.com", "Tech", "trending_up"),
        QuickShortcut("DuckDuckGo", "https://duckduckgo.com", "duckduckgo.com", "Privacy", "security")
    )

    fun currentActiveTab(): WebTab {
        val currentTabs = if (_isPrivateModeActive.value) _privateTabs.value else _regularTabs.value
        return currentTabs.find { it.id == _activeTabId.value }
            ?: currentTabs.firstOrNull()
            ?: defaultRegularTab
    }

    fun openOverlay(overlay: BrowserActiveOverlay) {
        _activeOverlay.value = overlay
    }

    fun closeOverlay() {
        _activeOverlay.value = BrowserActiveOverlay.NONE
    }

    fun setWallpaper(theme: WallpaperTheme) {
        _wallpaper.value = theme
    }

    fun setSearchEngine(engine: SearchEngine) {
        _searchEngine.value = engine
    }

    fun toggleJavaScript() {
        _isJavaScriptEnabled.update { !it }
    }

    fun selectTab(tabId: String) {
        _activeTabId.value = tabId
        _activeOverlay.value = BrowserActiveOverlay.NONE
    }

    fun createNewTab(
        url: String = "liquid://newtab",
        isPrivate: Boolean = _isPrivateModeActive.value
    ) {
        val domain = if (url == "liquid://newtab") "New Tab" else extractDomain(url)
        val title = if (url == "liquid://newtab") "New Tab" else domain
        val newTab = WebTab(
            title = title,
            url = url,
            displayDomain = domain,
            isPrivate = isPrivate,
            previewGradient = if (isPrivate) "dusk-sand" else "ice-blue",
            groupId = _activeGroupId.value
        )

        if (isPrivate) {
            _privateTabs.update { it + newTab }
            _isPrivateModeActive.value = true
        } else {
            _regularTabs.update { it + newTab }
        }
        _activeTabId.value = newTab.id
        _activeOverlay.value = BrowserActiveOverlay.NONE
    }

    fun closeTab(tabId: String) {
        if (_isPrivateModeActive.value) {
            val list = _privateTabs.value
            val closed = list.find { it.id == tabId }
            val remaining = list.filter { it.id != tabId }
            _privateTabs.value = remaining
            if (remaining.isEmpty()) {
                _isPrivateModeActive.value = false
                _activeTabId.value = _regularTabs.value.firstOrNull()?.id ?: run {
                    val fresh = defaultRegularTab
                    _regularTabs.value = listOf(fresh)
                    fresh.id
                }
            } else if (_activeTabId.value == tabId) {
                _activeTabId.value = remaining.last().id
            }
        } else {
            val list = _regularTabs.value
            val closed = list.find { it.id == tabId }
            if (closed != null && closed.url != "liquid://newtab") {
                _recentlyClosedTabs.update { (listOf(closed) + it).take(10) }
            }
            val remaining = list.filter { it.id != tabId }
            if (remaining.isEmpty()) {
                val fresh = defaultRegularTab
                _regularTabs.value = listOf(fresh)
                _activeTabId.value = fresh.id
            } else {
                _regularTabs.value = remaining
                if (_activeTabId.value == tabId) {
                    _activeTabId.value = remaining.last().id
                }
            }
        }
    }

    fun restoreLastClosedTab() {
        val recent = _recentlyClosedTabs.value
        if (recent.isNotEmpty()) {
            val toRestore = recent.first()
            _recentlyClosedTabs.value = recent.drop(1)
            _regularTabs.update { it + toRestore }
            _activeTabId.value = toRestore.id
        }
    }

    fun togglePrivateMode() {
        if (!_isPrivateModeActive.value) {
            _isPrivateModeActive.value = true
            if (_privateTabs.value.isEmpty()) {
                val newPrivateTab = WebTab(
                    title = "Private Tab",
                    url = "liquid://newtab",
                    displayDomain = "Private Tab",
                    isPrivate = true,
                    previewGradient = "dusk-sand"
                )
                _privateTabs.value = listOf(newPrivateTab)
                _activeTabId.value = newPrivateTab.id
            } else {
                _activeTabId.value = _privateTabs.value.first().id
            }
        } else {
            _isPrivateModeActive.value = false
            _activeTabId.value = _regularTabs.value.firstOrNull()?.id ?: run {
                val fresh = defaultRegularTab
                _regularTabs.value = listOf(fresh)
                fresh.id
            }
        }
    }

    fun navigateToUrl(rawInput: String) {
        val formatted = formatInputToUrl(rawInput)
        val domain = extractDomain(formatted)

        updateActiveTab {
            it.copy(
                url = formatted,
                displayDomain = domain,
                title = domain,
                isLoading = true,
                progress = 10
            )
        }
        _activeOverlay.value = BrowserActiveOverlay.NONE

        if (!_isPrivateModeActive.value) {
            viewModelScope.launch {
                repository.addHistory(title = domain, url = formatted, domain = domain)
            }
        }
    }

    fun updateTabUrl(newUrl: String, newDomain: String) {
        updateActiveTab {
            it.copy(
                url = newUrl,
                displayDomain = newDomain,
                isLoading = false
            )
        }
        if (!_isPrivateModeActive.value && (newUrl.startsWith("http://") || newUrl.startsWith("https://"))) {
            viewModelScope.launch {
                repository.addHistory(title = newDomain, url = newUrl, domain = newDomain)
            }
        }
    }

    fun updateTabTitle(newTitle: String) {
        updateActiveTab { it.copy(title = newTitle) }
    }

    fun updateTabProgress(progress: Int) {
        updateActiveTab {
            it.copy(
                progress = progress,
                isLoading = progress in 1..99
            )
        }
    }

    fun updateTabNavState(canGoBack: Boolean, canGoForward: Boolean) {
        updateActiveTab {
            it.copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
        }
    }

    fun toggleDesktopMode() {
        updateActiveTab { it.copy(isDesktopMode = !it.isDesktopMode) }
    }

    fun bookmarkCurrentTab() {
        val tab = currentActiveTab()
        if (tab.url.startsWith("http://") || tab.url.startsWith("https://")) {
            viewModelScope.launch {
                repository.addBookmark(
                    title = tab.title.ifEmpty { tab.displayDomain },
                    url = tab.url,
                    domain = tab.displayDomain
                )
            }
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch { repository.removeBookmark(id) }
    }

    fun removeBookmarkByUrl(url: String) {
        viewModelScope.launch { repository.removeBookmarkByUrl(url) }
    }

    fun removeHistory(id: Long) {
        viewModelScope.launch { repository.removeHistory(id) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    fun removeDownload(id: Long) {
        viewModelScope.launch { repository.removeDownload(id) }
    }

    fun recordDownload(fileName: String, url: String, mimeType: String, sizeBytes: Long) {
        viewModelScope.launch {
            repository.addDownload(fileName, url, "", mimeType, sizeBytes)
        }
    }

    fun createTabGroup(name: String, iconType: String = "stack") {
        val newGroup = TabGroup(
            name = name,
            iconType = iconType,
            count = 0
        )
        _tabGroups.update { it + newGroup }
        _activeGroupId.value = newGroup.id
    }

    fun selectTabGroup(group: TabGroup) {
        _activeGroupId.value = group.id
        closeOverlay()
    }

    fun clearBrowsingData(context: Context, clearCookies: Boolean, clearCache: Boolean, clearHist: Boolean) {
        if (clearCookies) {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
        }
        if (clearCache) {
            WebStorage.getInstance().deleteAllData()
        }
        if (clearHist) {
            clearAllHistory()
        }
    }

    private fun updateActiveTab(transform: (WebTab) -> WebTab) {
        val tabId = _activeTabId.value
        if (_isPrivateModeActive.value) {
            _privateTabs.update { list ->
                list.map { if (it.id == tabId) transform(it) else it }
            }
        } else {
            _regularTabs.update { list ->
                list.map { if (it.id == tabId) transform(it) else it }
            }
        }
    }

    private fun formatInputToUrl(input: String): String {
        val trimmed = input.trim()
        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> _searchEngine.value.searchUrlPrefix + Uri.encode(trimmed)
        }
    }

    private fun extractDomain(url: String): String {
        return try {
            val uri = Uri.parse(url)
            uri.host?.removePrefix("www.") ?: url
        } catch (_: Exception) {
            url
        }
    }
}

class BrowserViewModelFactory(
    private val repository: BrowserRepository,
    private val extensionManager: ManifestV3ExtensionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
            return BrowserViewModel(repository, extensionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
