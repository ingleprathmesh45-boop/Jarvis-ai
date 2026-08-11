package com.example.service

import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class JarvisNotificationService : NotificationListenerService() {
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn ?: return

        val packageName = sbn.packageName ?: return
        if (packageName == applicationContext.packageName) return // Ignore self notifications

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence("android.title")?.toString() ?: "Notification"
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (text.isEmpty()) return

        val appName = try {
            val pm = packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }

        scope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            db.notificationLogDao().insertLog(
                NotificationLogEntity(
                    packageName = packageName,
                    appName = appName,
                    title = title,
                    text = text,
                    suggestedReplies = "Thanks! | Got it | Talk soon"
                )
            )
        }
    }
}
