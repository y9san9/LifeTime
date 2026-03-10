package me.y9san9.lifetime.android.unlock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Receives ACTION_USER_PRESENT (phone unlocked) and starts the ephemeral
 * UnlockLauncherService, which in turn shows the waste-time popup.
 *
 * ACTION_USER_PRESENT fires after the keyguard is dismissed and is
 * delivered to manifest-registered receivers even in the background.
 */
class UnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return
        context.startForegroundService(
            Intent(context, UnlockLauncherService::class.java)
        )
    }
}
