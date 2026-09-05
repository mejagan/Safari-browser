package com.example.engine

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import java.io.ByteArrayInputStream

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ChromiumWebView(
    url: String,
    isPrivate: Boolean,
    isDesktopMode: Boolean,
    isJavaScriptEnabled: Boolean,
    extensionManager: ManifestV3ExtensionManager,
    modifier: Modifier = Modifier,
    onUrlChanged: (String) -> Unit = {},
    onTitleChanged: (String) -> Unit = {},
    onProgressChanged: (Int) -> Unit = {},
    onCanGoBackChanged: (Boolean) -> Unit = {},
    onCanGoForwardChanged: (Boolean) -> Unit = {},
    onDownloadStarted: (fileName: String, url: String, mimeType: String, sizeBytes: Long) -> Unit = { _, _, _, _ -> },
    onWebViewCreated: (WebView) -> Unit = {}
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = isJavaScriptEnabled
                domStorageEnabled = true
                databaseEnabled = true
                builtInZoomControls = true
                displayZoomControls = false
                loadWithOverviewMode = true
                useWideViewPort = true
                setSupportZoom(true)
                allowFileAccess = false
                allowContentAccess = false
                mediaPlaybackRequiresUserGesture = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
            }

            if (isPrivate) {
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                clearHistory()
                clearFormData()
            } else {
                settings.cacheMode = WebSettings.LOAD_DEFAULT
            }

            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(!isPrivate)
            cookieManager.setAcceptThirdPartyCookies(this, !isPrivate)
        }
    }

    // Handle desktop mode UA toggle
    LaunchedEffect(isDesktopMode) {
        if (isDesktopMode) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
            webView.settings.useWideViewPort = true
            webView.settings.loadWithOverviewMode = true
        } else {
            webView.settings.userAgentString = null // restores default Android WebView UA
        }
        if (webView.url != null) {
            webView.reload()
        }
    }

    // Handle JS toggle
    LaunchedEffect(isJavaScriptEnabled) {
        webView.settings.javaScriptEnabled = isJavaScriptEnabled
    }

    // Navigate to new URL when changed externally
    LaunchedEffect(url) {
        val current = webView.url
        if (url.isNotEmpty() && !url.startsWith("liquid://") && url != current) {
            webView.loadUrl(url)
        }
    }

    DisposableEffect(Unit) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                val reqUrl = request?.url?.toString() ?: return super.shouldInterceptRequest(view, request)
                if (extensionManager.isAdBlockerActive() && AdBlockEngine.shouldBlockUrl(reqUrl)) {
                    extensionManager.recordBlock(isTracker = true)
                    return WebResourceResponse(
                        "text/plain",
                        "UTF-8",
                        ByteArrayInputStream(ByteArray(0))
                    )
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let {
                    onUrlChanged(it)
                    onCanGoBackChanged(view?.canGoBack() ?: false)
                    onCanGoForwardChanged(view?.canGoForward() ?: false)
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                url?.let {
                    onUrlChanged(it)
                    onCanGoBackChanged(view?.canGoBack() ?: false)
                    onCanGoForwardChanged(view?.canGoForward() ?: false)
                }
                // Inject active MV3 content scripts (cosmetic ad filter)
                for (script in extensionManager.getActiveContentScripts()) {
                    view?.evaluateJavascript(script, null)
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val nextUrl = request?.url?.toString() ?: return false
                if (nextUrl.startsWith("http://") || nextUrl.startsWith("https://")) {
                    return false
                }
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                title?.let { onTitleChanged(it) }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                // Deny by default for privacy, or allow if user explicitly configures
                callback?.invoke(origin, false, false)
            }
        }

        webView.setDownloadListener { downloadUrl, userAgent, contentDisposition, mimeType, contentLength ->
            try {
                val fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType)
                val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                    setMimeType(mimeType)
                    setDescription("Downloading file with Liquid Browser")
                    setTitle(fileName)
                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                }
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                dm?.enqueue(request)
                Toast.makeText(context, "Downloading $fileName", Toast.LENGTH_SHORT).show()
                onDownloadStarted(fileName, downloadUrl, mimeType, contentLength)
            } catch (e: Exception) {
                Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }

        onWebViewCreated(webView)

        onDispose {
            if (isPrivate) {
                webView.clearCache(true)
                webView.clearFormData()
                webView.clearHistory()
            }
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier
    )
}
