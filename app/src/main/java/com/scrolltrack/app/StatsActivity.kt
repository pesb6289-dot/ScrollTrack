package com.scrolltrack.app

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.format.TextStyle
import kotlin.math.roundToInt

class StatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val store = ScrollStore(this)
        val days = store.lastDays(7)
        val values = days.map { it.second }
        val labels = days.map { it.first.dayOfWeek.getDisplayName(TextStyle.SHORT, TR_LOCALE) }

        val today = values.last()
        val yesterday = store.countFor(LocalDate.now().minusDays(1))
        val total = values.sum()
        val average = (total / 7.0).roundToInt()
        val maxV = values.maxOrNull() ?: 0

        animateNumber(findViewById<TextView>(R.id.tvToday), 0, today, 800)
        animateNumber(findViewById<TextView>(R.id.tvYesterday), 0, yesterday, 800)
        animateNumber(findViewById<TextView>(R.id.tvWeekTotal), 0, total, 800)
        animateNumber(findViewById<TextView>(R.id.tvAverage), 0, average, 800)

        findViewById<TextView>(R.id.tvBusiest).text =
            if (maxV > 0) {
                val d = days[values.indexOf(maxV)].first
                "${d.dayOfWeek.getDisplayName(TextStyle.FULL, TR_LOCALE)} · $maxV video"
            } else {
                "Henüz veri yok"
            }

        findViewById<WeekBarChartView>(R.id.chart).setData(labels, values)

        val root = findViewById<LinearLayout>(R.id.contentRoot)
        animateEntrance((0 until root.childCount).map { root.getChildAt(it) })
    }
}
