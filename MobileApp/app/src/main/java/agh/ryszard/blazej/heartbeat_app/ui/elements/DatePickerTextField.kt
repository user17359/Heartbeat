package agh.ryszard.blazej.heartbeat_app.ui.elements

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.text.SimpleDateFormat
import java.util.Date

@Composable
@ExperimentalMaterial3Api
fun DatePickerTextField (onClick: () -> Unit,
                         modifier: Modifier = Modifier,
                         isDateSelected: Boolean,
                         date: DatePickerState,
                         leadingIcon: @Composable () -> Unit,
                         label: String){
    OutlinedTextField(
        value = if(isDateSelected) {
            val formatter = SimpleDateFormat("dd/MM/yyyy")
            val selectedDate = Date(date.selectedDateMillis!!)
            formatter.format(selectedDate)
        }
        else
            "",
        enabled = false,
        onValueChange = {},
        label = { Text(label) },
        leadingIcon = leadingIcon,
        // workaround to create text field + time picker
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = colorScheme.onSurface,
            disabledBorderColor = colorScheme.outline,
            disabledLeadingIconColor = colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = colorScheme.onSurfaceVariant,
            disabledLabelColor = colorScheme.onSurfaceVariant,
            disabledPlaceholderColor = colorScheme.onSurfaceVariant,
        ),
        modifier = modifier
            .noRippleClickable {
                onClick()
            }
    )
}