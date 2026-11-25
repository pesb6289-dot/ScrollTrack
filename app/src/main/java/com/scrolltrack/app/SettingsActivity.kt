package com.scrolltrack.app

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {

    private lateinit var store: ScrollStore
    private lateinit var tvStatus: TextView
    private lateinit var tvGoal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        store = ScrollStore(this)

        tvStatus = findViewById(R.id.tvStatus)
        tvGoal = findViewById(R.id.tvGoal)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<View>(R.id.btnAccessibility).setOnClickListener {
            AccessibilityHelper.openAccessibilitySettings(this)
        }
        findViewById<View>(R.id.btnGoal).setOnClickListener {
            GoalDialog.show(this, store) { refresh() }
        }
        findViewById<View>(R.id.btnReset).setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Tüm geçmiş silinsin mi?")
                .setMessage("Bugünün ve geçmiş günlerin tüm sayaç verileri kalıcı olarak silinir. Bu işlem geri alınamaz.")
                .setPositiveButton("Sil") { _, _ ->
                    store.clearHistory()
                    Toast.makeText(this, "Geçmiş silindi.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Vazgeç", null)
                .show()
        }

        val root = findViewById<LinearLayout>(R.id.contentRoot)
        animateEntrance((0 until root.childCount).map { root.getChildAt(it) })
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val enabled = AccessibilityHelper.isServiceEnabled(this)
        tvStatus.text = if (enabled) "Açık — sayaç çalışıyor" else "Kapalı — sayaç çalışmıyor"
        tvStatus.setTextColor(
            ContextCompat.getColor(this, if (enabled) R.color.success else R.color.warn)
        )
        tvGoal.text = store.goal.toString()
    }
}
