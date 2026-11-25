package com.scrolltrack.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var store: ScrollStore
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var tvCounter: TextView
    private lateinit var tvGoal: TextView
    private lateinit var tvPercent: TextView
    private lateinit var tvRemaining: TextView
    private lateinit var tvYesterday: TextView
    private lateinit var tvAvg: TextView
    private lateinit var tvWeek: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var warningCard: View

    private var shownCount = 0
    private var shownProgress = 0
    private var ready = false

    private val ticker = object : Runnable {
        override fun run() {
            refresh()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = ScrollStore(this)

        if (!store.onboardingDone) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        tvCounter = findViewById(R.id.tvCounter)
        tvGoal = findViewById(R.id.tvGoal)
        tvPercent = findViewById(R.id.tvPercent)
        tvRemaining = findViewById(R.id.tvRemaining)
        tvYesterday = findViewById(R.id.tvYesterday)
        tvAvg = findViewById(R.id.tvAvg)
        tvWeek = findViewById(R.id.tvWeek)
        progressBar = findViewById(R.id.progressBar)
        warningCard = findViewById(R.id.warningCard)

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        findViewById<View>(R.id.btnStats).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
        findViewById<View>(R.id.btnEnable).setOnClickListener {
            AccessibilityHelper.openAccessibilitySettings(this)
        }
        findViewById<View>(R.id.btnEditGoal).setOnClickListener {
            GoalDialog.show(this, store) { refresh() }
        }

        val root = findViewById<LinearLayout>(R.id.contentRoot)
        val cards = (0 until root.childCount).map { root.getChildAt(it) }
        animateEntrance(cards)
        ready = true
    }

    override fun onResume() {
        super.onResume()
        if (!ready) return
        handler.removeCallbacks(ticker)
        handler.post(ticker)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(ticker)
    }

    private fun refresh() {
        val today = store.countFor(LocalDate.now())
        val goal = max(store.goal, 1)

        // Sayaç
        if (today != shownCount) {
            animateNumber(tvCounter, shownCount, today, 500)
            shownCount = today
        }

        // Hedef + ilerleme
        tvGoal.text = goal.toString()
        val percent = (today.toLong() * 100 / goal).toInt()
        tvPercent.text = "İlerleme: %$percent"
        val remaining = goal - today
        tvRemaining.text = if (remaining > 0) "Kalan: $remaining" else "Hedefe ulaşıldı"
        val color = if (percent >= 100) R.color.warn else R.color.text
        tvPercent.setTextColor(ContextCompat.getColor(this, color))

        val target = min(1000, (today.toLong() * 1000 / goal).toInt())
        if (target != shownProgress) {
            ObjectAnimator.ofInt(progressBar, "progress", shownProgress, target).apply {
                duration = 600
                start()
            }
            shownProgress = target
        }

        // Özet
        val days = store.lastDays(7)
        val total = days.sumOf { it.second }
        tvYesterday.text = store.countFor(LocalDate.now().minusDays(1)).toString()
        tvAvg.text = (total / 7).toString()
        tvWeek.text = total.toString()

        // İzin durumu
        warningCard.visibility =
            if (AccessibilityHelper.isServiceEnabled(this)) View.GONE else View.VISIBLE
    }
}
