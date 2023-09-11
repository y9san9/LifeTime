package me.y9san9.lifetime.android

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Activity.requestPermissions(vararg permissions: String, requestCode: Int) {
    requestPermissions(permissions, requestCode)
}
