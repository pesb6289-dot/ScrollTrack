package com.scrolltrack.app

import android.content.Context
import java.time.LocalDate

/**
 * Tüm veriler yalnızca cihazda (SharedPreferences) tutulur.
 * Her gün için ayrı anahtar kullanılır: "c_2026-11-25".
 * Gün değişince yeni anahtar otomatik başlar; eski günler silinmez.
 */
class ScrollStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("scrolltrack_data", Context.MODE_PRIVATE)

    companion object {
        private val LOCK = Any()
        private const val KEY_GOAL = "goal"
        private const val KEY_ONBOARDING = "onboarding_done"
        private const val DAY_PREFIX = "c_"
    }

    var goal: Int
        get() = prefs.getInt(KEY_GOAL, 300)
        set(value) = prefs.edit().putInt(KEY_GOAL, value).apply()

    var onboardingDone: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING, value).apply()

    fun countFor(date: LocalDate): Int = prefs.getInt(DAY_PREFIX + date.toString(), 0)

    /** Bugünün sayacını 1 artırır (gün, çağrı anındaki tarihe göre belirlenir). */
    fun increment(): Int = synchronized(LOCK) {
        val key = DAY_PREFIX + LocalDate.now().toString()
        val newValue = prefs.getInt(key, 0) + 1
        prefs.edit().putInt(key, newValue).apply()
        newValue
    }

    /** Bugünden geriye doğru n gün (en eski önce, bugün en sonda). */
    fun lastDays(n: Int): List<Pair<LocalDate, Int>> {
        val today = LocalDate.now()
        return (n - 1 downTo 0).map { i ->
            val d = today.minusDays(i.toLong())
            d to countFor(d)
        }
    }

    /** Yalnızca sayaç geçmişini siler; hedef ve tanıtım durumu korunur. */
    fun clearHistory() = synchronized(LOCK) {
        val editor = prefs.edit()
        prefs.all.keys.filter { it.startsWith(DAY_PREFIX) }.forEach { editor.remove(it) }
        editor.apply()
    }
}
