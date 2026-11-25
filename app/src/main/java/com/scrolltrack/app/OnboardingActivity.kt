package com.scrolltrack.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var store: ScrollStore
    private lateinit var pages: List<View>
    private lateinit var tvStep: TextView
    private lateinit var btnPrimary: TextView
    private lateinit var btnBack: TextView
    private lateinit var btnSkip: TextView
    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        store = ScrollStore(this)

        pages = listOf(
            findViewById<View>(R.id.page1),
            findViewById<View>(R.id.page2),
            findViewById<View>(R.id.page3)
        )
        tvStep = findViewById(R.id.tvStep)
        btnPrimary = findViewById(R.id.btnPrimary)
        btnBack = findViewById(R.id.btnBack)
        btnSkip = findViewById(R.id.btnSkip)

        btnPrimary.setOnClickListener {
            if (step < 2) {
                showStep(step + 1)
            } else {
                AccessibilityHelper.openAccessibilitySettings(this)
            }
        }
        btnBack.setOnClickListener { if (step > 0) showStep(step - 1) }
        btnSkip.setOnClickListener { finishOnboarding() }
        findViewById<View>(R.id.btnAppInfo).setOnClickListener {
            AccessibilityHelper.openAppInfo(this)
        }

        showStep(savedInstanceState?.getInt("step") ?: 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("step", step)
    }

    override fun onResume() {
        super.onResume()
        // Kullanıcı izni açıp geri döndüyse sayaç ekranına geç.
        if (AccessibilityHelper.isServiceEnabled(this)) finishOnboarding()
    }

    private fun showStep(newStep: Int) {
        step = newStep
        pages.forEachIndexed { i, p -> p.visibility = if (i == step) View.VISIBLE else View.GONE }
        val page = pages[step]
        page.alpha = 0f
        page.translationY = dp(24).toFloat()
        page.animate().alpha(1f).translationY(0f).setDuration(350).start()

        tvStep.text = "${step + 1} / 3"
        btnPrimary.text = if (step < 2) "Devam" else "Erişilebilirlik ayarlarını aç"
        btnBack.visibility = if (step > 0) View.VISIBLE else View.INVISIBLE
        btnSkip.visibility = if (step == 2) View.VISIBLE else View.INVISIBLE
    }

    private fun finishOnboarding() {
        store.onboardingDone = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
