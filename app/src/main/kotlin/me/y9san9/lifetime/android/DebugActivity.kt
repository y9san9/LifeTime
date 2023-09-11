package me.y9san9.lifetime.android

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.y9san9.lifetime.R


class DebugActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stacktrace = intent.getStringExtra("stacktrace")
            ?: error("Use DebugActivity.Companion.intent() to construct intents for the activity")

        setContent {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = getString(R.string.app_stopped),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(20.dp))
                Text(
                    text = getString(R.string.crash_reason, stacktrace),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple()
                    ) { copyToClipboard(stacktrace) }
                        .fillMaxWidth()
                )
            }
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.lifetime_crash_title), text)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        fun intent(packageContext: Context, throwable: Throwable): Intent =
            intent(packageContext, throwable.stackTraceToString())

        fun intent(packageContext: Context, stacktrace: String): Intent =
            Intent(packageContext, DebugActivity::class.java).apply {
                flags = FLAG_ACTIVITY_NEW_TASK
                putExtra("stacktrace", stacktrace)
            }
    }
}
