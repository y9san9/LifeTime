package me.y9san9.lifetime.statistics.compose

import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.ui.res.stringResource
import me.y9san9.lifetime.feature.statistics.R
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth

@Composable
fun SettingsContent(
    hoursPerDay: String,
    onHoursPerDayChange: (String) -> Unit,
    showHoursPerDayError: Boolean,
) {
    Text(
        text = stringResource(R.string.settings_title),
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.h5,
    )

    Spacer(Modifier.size(16.dp))

     OutlinedTextField(
        value = hoursPerDay,
        onValueChange = onHoursPerDayChange,
        label = {
            Text(stringResource(R.string.hours_per_day))
        },
        isError = showHoursPerDayError,
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colors.onBackground,
        ),
    )

}
