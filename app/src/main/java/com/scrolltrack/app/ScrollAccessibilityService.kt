package com.scrolltrack.app

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

/**
 * Yalnızca TikTok paketlerinden gelen "kaydırıldı" sinyalini dinler.
 * Ekran içeriğine, metne, mesajlara veya kullanıcı adlarına BAKMAZ
 * (canRetrieveWindowContent = false; event.text vb. hiç okunmaz).
 *
 * Çift sayımı önlemek için "sessizlik penceresi" yöntemi kullanılır:
 * Bir kaydırma hareketi onlarca olay üretir. Olaylar durup QUIET_MS boyunca
 * yeni olay gelmezse hareket bitmiş sayılır ve sayaç TEK KEZ artar.
 */
class ScrollAccessibilityService : AccessibilityService() {

    companion object {
        val TIKTOK_PACKAGES = setOf(
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill"
        )
        /** Bu kadar süre olay gelmezse kaydırma bitmiş sayılır. */
        const val QUIET_MS = 450L
        /** Olaylar hiç durmasa bile bir hareket en fazla bu kadar sürer. */
        const val MAX_BURST_MS = 1500L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val store by lazy { ScrollStore(applicationContext) }

    private var pending = false
    private var burstStart = 0L

    private val countRunnable = Runnable {
        pending = false
        store.increment()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        if (event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg !in TIKTOK_PACKAGES) return

        val now = SystemClock.elapsedRealtime()
        if (!pending) {
            pending = true
            burstStart = now
        }
        handler.removeCallbacks(countRunnable)
        val wait = if (now - burstStart >= MAX_BURST_MS) 0L else QUIET_MS
        handler.postDelayed(countRunnable, wait)
    }

    override fun onInterrupt() {
        cancelPending()
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        cancelPending()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        cancelPending()
        super.onDestroy()
    }

    private fun cancelPending() {
        handler.removeCallbacks(countRunnable)
        pending = false
    }
}
