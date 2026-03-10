package me.y9san9.lifetime.android.unlock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import me.y9san9.lifetime.android.UnlockPopupActivity

/**
 * Receives ACTION_USER_PRESENT (phone unlocked) and shows the waste-time popup.
 *
 * ACTION_USER_PRESENT fires after the keyguard is dismissed and is
 * delivered to manifest-registered receivers even in the background.
 *
 * Android grants a background activity launch exemption for the duration of
 * onReceive() when the broadcast was sent by the system, so we start the
 * activity directly here — no bridge service needed.
 */
class UnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return
        context.startActivity(
            Intent(context, UnlockPopupActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
    }
}
