package me.y9san9.lifetime.android.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissions(vararg permissions: String, requestCode: Int) {
    requestPermissions(permissions, requestCode)
}

/**
 * Should be only called from `onResume`.
 * This is made due to a known bug in android:
 * https://issuetracker.google.com/issues/113122354#comment20
 */
fun Context.startServiceOnResume(intent: Intent) {
    try {
        startService(intent)
    } catch (_: IllegalStateException) {
        // Do nothing when exception occur.
        // According to the response from Google:
        //
        // "If the device hasn’t fully awake, activities
        // would be paused immediately and eventually be
        // resumed again after its fully awake."
        //
        // I assume that on resume WILL be called again in that case.
        return
    }
}
