package com.example.engine

import android.net.Uri

object AdBlockEngine {

    // Common tracking & ad-serving domains
    private val TRACKER_DOMAINS = setOf(
        "doubleclick.net",
        "google-analytics.com",
        "googlesyndication.com",
        "adservice.google.com",
        "pagead2.googlesyndication.com",
        "adnxs.com",
        "criteo.com",
        "rubiconproject.com",
        "pubmatic.com",
        "outbrain.com",
        "taboola.com",
        "scorecardresearch.com",
        "quantserve.com",
        "facebook.net",
        "connect.facebook.net",
        "ads.twitter.com",
        "analytics.twitter.com",
        "amazon-adsystem.com",
        "adroll.com",
        "openx.net",
        "casalemedia.com",
        "moatads.com",
        "advertising.com",
        "zedo.com",
        "chartbeat.com",
        "hotjar.com",
        "segment.io",
        "mixpanel.com",
        "clarity.ms",
        "branch.io",
        "appsflyer.com"
    )

    // Keywords in URLs often indicative of tracking / ad pixels
    private val AD_URL_PATTERNS = listOf(
        "/ads/",
        "/adserver/",
        "/adview",
        "/pagead/",
        "/track.gif",
        "/pixel.gif",
        "/beacon.gif",
        "banner_ad",
        "sponsored_link",
        "telemetry."
    )

    // Cosmetic CSS to hide common ad slots and banner overlays
    val COSMETIC_AD_BLOCK_CSS = """
        [id*='google_ads_'],
        [id*='gpt-passback'],
        [class*='ad-banner'],
        [class*='ad-container'],
        [class*='adsbygoogle'],
        [class*='ad-placeholder'],
        [class*='ad-unit'],
        [class*='advertisement'],
        [class*='taboola-'],
        [class*='outbrain-'],
        [id*='taboola-'],
        [id*='outbrain-'],
        .ad-slot,
        .ad-wrapper,
        .sponsored-content,
        .trc_related_container,
        iframe[src*='doubleclick.net'],
        iframe[src*='googlesyndication.com'] {
            display: none !important;
            visibility: hidden !important;
            height: 0 !important;
            min-height: 0 !important;
            opacity: 0 !important;
            pointer-events: none !important;
        }
    """.trimIndent()

    val COSMETIC_INJECTION_JS = """
        (function() {
            if (document.getElementById('liquid-guard-style')) return;
            var style = document.createElement('style');
            style.id = 'liquid-guard-style';
            style.type = 'text/css';
            style.appendChild(document.createTextNode(`$COSMETIC_AD_BLOCK_CSS`));
            (document.head || document.documentElement).appendChild(style);
        })();
    """.trimIndent()

    /**
     * Checks whether an intercepted network request should be blocked.
     */
    fun shouldBlockUrl(url: String): Boolean {
        try {
            val uri = Uri.parse(url)
            val host = uri.host?.lowercase() ?: return false

            // Domain exact or suffix match
            for (tracker in TRACKER_DOMAINS) {
                if (host == tracker || host.endsWith(".$tracker")) {
                    return true
                }
            }

            // Pattern check
            val path = uri.path?.lowercase() ?: ""
            for (pattern in AD_URL_PATTERNS) {
                if (path.contains(pattern)) {
                    return true
                }
            }
        } catch (_: Exception) {
            return false
        }
        return false
    }

    val ruleCount: Int get() = TRACKER_DOMAINS.size + AD_URL_PATTERNS.size
}
