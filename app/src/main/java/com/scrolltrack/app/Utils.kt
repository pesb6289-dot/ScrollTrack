package com.scrolltrack.app

import android.animation.ValueAnimator
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

val TR_LOCALE: Locale = Locale("tr", "TR")

fun Context.dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

/** Sayıyı akıcı şekilde yukarı/aşağı sayarak gösterir. */
fun animateNumber(
    textView: TextView,
    from: Int,
    to: Int,
    durationMs: Long = 600,
    format: (Int) -> String = { it.toString() }
) {
    if (from == to) {
        textView.text = format(to)
        return
    }
    ValueAnimator.ofInt(from, to).apply {
        duration = durationMs
        interpolator = DecelerateInterpolator()
        addUpdateListener { textView.text = format(it.animatedValue as Int) }
        start()
    }
}

/** Kartları sırayla aşağıdan yukarı doğru belirginleştirir. */
fun animateEntrance(views: List<View>) {
    views.forEachIndexed { i, v ->
        v.alpha = 0f
        v.translationY = v.context.dp(24).toFloat()
        v.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(i * 70L)
            .setDuration(450)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}

object GoalDialog {
    fun show(activity: AppCompatActivity, store: ScrollStore, onChanged: () -> Unit) {
        val input = EditText(activity).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            filters = arrayOf(InputFilter.LengthFilter(5))
            setText(store.goal.toString())
            setSelection(text.length)
        }
        val container = FrameLayout(activity).apply {
            setPadding(activity.dp(24), activity.dp(8), activity.dp(24), 0)
            addView(input)
        }
        MaterialAlertDialogBuilder(activity)
            .setTitle("Günlük hedef")
            .setMessage("Bir gün için belirlemek istediğin video sayısını yaz.")
            .setView(container)
            .setPositiveButton("Kaydet") { _, _ ->
                val v = input.text.toString().toIntOrNull()
                if (v != null && v > 0) {
                    store.goal = v
                    onChanged()
                } else {
                    Toast.makeText(activity, "Lütfen 1 veya daha büyük bir sayı gir.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }
}
