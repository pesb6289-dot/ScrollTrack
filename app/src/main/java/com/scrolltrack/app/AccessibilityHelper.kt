package com.scrolltrack.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object AccessibilityHelper {

    fun isServiceEnabled(context: Context): Boolean {
        val cn = ComponentName(context, ScrollAccessibilityService::class.java)
        val full = cn.flattenToString()
        val short = cn.flattenToShortString()
        val setting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return setting.split(':').any {
            it.equals(full, ignoreCase = true) || it.equals(short, ignoreCase = true)
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            context.startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {
            context.startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    fun openAppInfo(context: Context) {
        try {
            context.startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + context.packageName))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: Exception) {
            openAccessibilitySettings(context)
        }
    }
}
