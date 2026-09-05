package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.engine.ChromiumWebView
import com.example.model.WebTab
import com.example.ui.components.ActionBottomSheet
import com.example.ui.components.AndroidStatusBar
import com.example.ui.components.LiquidOmnibar
import com.example.ui.components.SearchOverlay
import com.example.ui.theme.ElectricBlue
import com.example.viewmodel.BrowserActiveOverlay
import com.example.viewmodel.BrowserViewModel

@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    val regularTabs by viewModel.regularTabs.collectAsStateWithLifecycle()
    val privateTabs by viewModel.privateTabs.collectAsStateWithLifecycle()
    val activeTabId by viewModel.activeTabId.collectAsStateWithLifecycle()
    val isPrivateMode by viewModel.isPrivateModeActive.collectAsStateWithLifecycle()
    val activeOverlay by viewModel.activeOverlay.collectAsStateWithLifecycle()
    val wallpaper by viewModel.wallpaper.collectAsStateWithLifecycle()
    val searchEngine by viewModel.searchEngine.collectAsStateWithLifecycle()
    val isJavaScriptEnabled by viewModel.isJavaScriptEnabled.collectAsStateWithLifecycle()
    val recentlyClosed by viewModel.recentlyClosedTabs.collectAsStateWithLifecycle()
    val bookmarks by viewModel.bookmarks.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val extensions by viewModel.extensions.collectAsStateWithLifecycle()
    val privacyReport by viewModel.privacyReport.collectAsStateWithLifecycle()
    val tabGroups by viewModel.tabGroups.collectAsStateWithLifecycle()
    val activeGroupId by viewModel.activeGroupId.collectAsStateWithLifecycle()

    val currentTab = viewModel.currentActiveTab()
    val isNewTab = currentTab.url == "liquid://newtab"
    val allTabs = if (isPrivateMode) privateTabs else regularTabs
    val activeGroup = tabGroups.find { it.id == activeGroupId } ?: tabGroups.first()

    // Handle Android system back gesture
    BackHandler {
        when {
            activeOverlay != BrowserActiveOverlay.NONE -> viewModel.closeOverlay()
            webViewInstance?.canGoBack() == true -> webViewInstance?.goBack()
            !isNewTab -> viewModel.navigateToUrl("liquid://newtab")
            else -> {
                // If on new tab and regular tabs exist, minimize or exit gracefully
                (context as? android.app.Activity)?.finish()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Main Browser Viewport
        if (isNewTab) {
            NewTabHomeScreen(
                wallpaper = wallpaper,
                shortcuts = viewModel.quickShortcuts,
                bookmarks = bookmarks,
                recentlyClosed = recentlyClosed,
                privacyReport = privacyReport,
                onNavigate = { url -> viewModel.navigateToUrl(url) },
                onSearchClick = { viewModel.openOverlay(BrowserActiveOverlay.SEARCH) },
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                onRestoreTab = { viewModel.restoreLastClosedTab() },
                onOpenBookmarks = { viewModel.openOverlay(BrowserActiveOverlay.BOOKMARKS) },
                onOpenHistory = { viewModel.openOverlay(BrowserActiveOverlay.HISTORY) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ChromiumWebView(
                url = currentTab.url,
                isPrivate = currentTab.isPrivate,
                isDesktopMode = currentTab.isDesktopMode,
                isJavaScriptEnabled = isJavaScriptEnabled,
                extensionManager = viewModel.extensionManager,
                modifier = Modifier.fillMaxSize(),
                onUrlChanged = { newUrl ->
                    val domain = try {
                        Uri.parse(newUrl).host?.removePrefix("www.") ?: newUrl
                    } catch (_: Exception) {
                        newUrl
                    }
                    viewModel.updateTabUrl(newUrl, domain)
                },
                onTitleChanged = { title ->
                    viewModel.updateTabTitle(title)
                },
                onProgressChanged = { progress ->
                    viewModel.updateTabProgress(progress)
                },
                onCanGoBackChanged = { canBack ->
                    viewModel.updateTabNavState(canBack, currentTab.canGoForward)
                },
                onCanGoForwardChanged = { canForward ->
                    viewModel.updateTabNavState(currentTab.canGoBack, canForward)
                },
                onDownloadStarted = { fileName, url, mimeType, sizeBytes ->
                    viewModel.recordDownload(fileName, url, mimeType, sizeBytes)
                },
                onWebViewCreated = { wv ->
                    webViewInstance = wv
                }
            )
        }

        // Top Android Status Bar
        AndroidStatusBar(
            isDarkText = !isPrivateMode && wallpaper != com.example.model.WallpaperTheme.DEEP_DARK && wallpaper != com.example.model.WallpaperTheme.PINE_GREEN,
            isPrivateMode = isPrivateMode,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Loading Progress Bar (under status bar)
        if (currentTab.isLoading && !isNewTab) {
            LinearProgressIndicator(
                progress = { currentTab.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(2.5.dp)
                    .align(Alignment.TopCenter),
                color = ElectricBlue,
                trackColor = Color.Transparent
            )
        }

        // Bottom Floating Liquid Omnibar
        LiquidOmnibar(
            domain = if (isNewTab) "Search or enter address" else currentTab.displayDomain,
            canGoBack = currentTab.canGoBack || !isNewTab,
            isLoading = currentTab.isLoading,
            tabCount = allTabs.size,
            isPrivate = isPrivateMode,
            onBackClick = {
                if (webViewInstance?.canGoBack() == true) {
                    webViewInstance?.goBack()
                } else if (!isNewTab) {
                    viewModel.navigateToUrl("liquid://newtab")
                }
            },
            onOmnibarClick = {
                viewModel.openOverlay(BrowserActiveOverlay.SEARCH)
            },
            onTabsClick = {
                viewModel.openOverlay(BrowserActiveOverlay.TABS_OVERVIEW)
            },
            onReloadClick = {
                if (currentTab.isLoading) {
                    webViewInstance?.stopLoading()
                    viewModel.updateTabProgress(0)
                } else {
                    webViewInstance?.reload()
                }
            },
            onMoreClick = {
                viewModel.openOverlay(BrowserActiveOverlay.ACTIONS)
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // --- Animated Overlays ---

        // Search Overlay
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.SEARCH,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 3 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 3 })
        ) {
            SearchOverlay(
                initialQuery = if (isNewTab) "" else currentTab.url,
                currentEngine = searchEngine,
                bookmarks = bookmarks,
                history = history,
                onNavigate = { target ->
                    viewModel.navigateToUrl(target)
                },
                onDismiss = { viewModel.closeOverlay() }
            )
        }

        // Action Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.ACTIONS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ActionBottomSheet(
                url = currentTab.url,
                isDesktopMode = currentTab.isDesktopMode,
                privacyReport = privacyReport,
                onDismiss = { viewModel.closeOverlay() },
                onCopyLink = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", currentTab.url))
                    Toast.makeText(context, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
                },
                onShare = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, currentTab.url)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share via"))
                },
                onAddBookmark = {
                    viewModel.bookmarkCurrentTab()
                    Toast.makeText(context, "Added to Bookmarks", Toast.LENGTH_SHORT).show()
                },
                onToggleDesktop = {
                    viewModel.toggleDesktopMode()
                },
                onOpenBookmarks = { viewModel.openOverlay(BrowserActiveOverlay.BOOKMARKS) },
                onOpenHistory = { viewModel.openOverlay(BrowserActiveOverlay.HISTORY) },
                onOpenDownloads = { viewModel.openOverlay(BrowserActiveOverlay.DOWNLOADS) },
                onOpenExtensions = { viewModel.openOverlay(BrowserActiveOverlay.EXTENSIONS) },
                onOpenSettings = { viewModel.openOverlay(BrowserActiveOverlay.SETTINGS) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Tabs Overview Screen
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.TABS_OVERVIEW,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            TabsOverviewScreen(
                tabs = allTabs,
                activeTabId = activeTabId,
                currentGroupName = if (isPrivateMode) "Private Mode" else activeGroup.name,
                onSelectTab = { tabId -> viewModel.selectTab(tabId) },
                onCloseTab = { tabId -> viewModel.closeTab(tabId) },
                onNewTab = { viewModel.createNewTab() },
                onOpenTabGroups = { viewModel.openOverlay(BrowserActiveOverlay.TAB_GROUPS) },
                onOpenPrivateMode = { viewModel.togglePrivateMode() },
                onDone = { viewModel.closeOverlay() }
            )
        }

        // Tab Groups Drawer
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.TAB_GROUPS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            TabGroupsDrawer(
                groups = tabGroups,
                activeGroupId = activeGroupId,
                currentTabCount = allTabs.size,
                onSelectGroup = { group -> viewModel.selectTabGroup(group) },
                onCreateGroup = { name -> viewModel.createTabGroup(name) },
                onClose = { viewModel.openOverlay(BrowserActiveOverlay.TABS_OVERVIEW) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Bookmarks Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.BOOKMARKS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            BookmarksSheet(
                bookmarks = bookmarks,
                onSelectBookmark = { url ->
                    viewModel.navigateToUrl(url)
                    viewModel.closeOverlay()
                },
                onDeleteBookmark = { id -> viewModel.removeBookmark(id) },
                onClose = { viewModel.closeOverlay() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // History Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.HISTORY,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            HistorySheet(
                history = history,
                onSelectHistory = { url ->
                    viewModel.navigateToUrl(url)
                    viewModel.closeOverlay()
                },
                onDeleteHistory = { id -> viewModel.removeHistory(id) },
                onClearAll = { viewModel.clearAllHistory() },
                onClose = { viewModel.closeOverlay() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Downloads Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.DOWNLOADS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            DownloadsSheet(
                downloads = downloads,
                onDeleteDownload = { id -> viewModel.removeDownload(id) },
                onClose = { viewModel.closeOverlay() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Extensions Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.EXTENSIONS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ExtensionsSheet(
                extensions = extensions,
                onToggleExtension = { id -> viewModel.extensionManager.toggleExtension(id) },
                onClose = { viewModel.closeOverlay() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Settings Sheet
        AnimatedVisibility(
            visible = activeOverlay == BrowserActiveOverlay.SETTINGS,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            SettingsSheet(
                currentSearchEngine = searchEngine,
                currentWallpaper = wallpaper,
                isJavaScriptEnabled = isJavaScriptEnabled,
                isAdBlockerActive = viewModel.extensionManager.isAdBlockerActive(),
                onSelectSearchEngine = { viewModel.setSearchEngine(it) },
                onSelectWallpaper = { viewModel.setWallpaper(it) },
                onToggleJavaScript = { viewModel.toggleJavaScript() },
                onToggleAdBlocker = { viewModel.extensionManager.toggleExtension("liquid-guard-mv3") },
                onClearBrowsingData = {
                    viewModel.clearBrowsingData(context, clearCookies = true, clearCache = true, clearHist = true)
                },
                onClose = { viewModel.closeOverlay() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
