package org.mozilla.focus.browser.binding

import android.webkit.URLUtil
import android.widget.ImageView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import mozilla.components.browser.state.selector.findTabOrCustomTabOrSelectedTab
import mozilla.components.browser.state.state.BrowserState
import mozilla.components.browser.state.state.SessionState
import mozilla.components.browser.state.store.BrowserStore
import mozilla.components.support.ktx.kotlinx.coroutines.flow.ifAnyChanged
import org.mozilla.focus.R

class SecurityInfoBinding(
    store: BrowserStore,
    private val tabId: String,
    private val securityView: ImageView
) : AbstractBinding(store) {
    override suspend fun onState(flow: Flow<BrowserState>) {
        flow.mapNotNull { state -> state.findTabOrCustomTabOrSelectedTab(tabId) }
            .ifAnyChanged { tab -> arrayOf(tab.content.securityInfo, tab.content.loading) }
            .collect { tab -> onSecurityStateChanged(tab) }
    }

    private fun onSecurityStateChanged(tab: SessionState) {
        if (tab.content.securityInfo.secure) {
            securityView.setImageResource(R.drawable.ic_lock)
        } else {
            if (URLUtil.isHttpUrl(tab.content.url)) {
                // HTTP site
                securityView.setImageResource(R.drawable.ic_internet)
            } else {
                // Certificate is bad
                securityView.setImageResource(R.drawable.ic_warning)
            }
        }
    }
}