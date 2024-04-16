package agh.ryszard.blazej.heartbeat_app.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
@ExperimentalMaterial3Api
fun DataPickerPreview(){
    val dps = rememberDatePickerState()
    DatePickerDialogWrapped({}, dps)
}

@Composable
@ExperimentalMaterial3Api
fun DatePickerDialogWrapped(onDismiss: () -> Unit, datePickerState: DatePickerState){
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {TextButton(onClick = onDismiss) {
                Text(text = "Confirm")
        }},
        dismissButton = {TextButton(onClick = onDismiss) {
            Text(text = "Dismiss")
        }},
        content = {DatePicker(state = datePickerState, modifier = Modifier.fillMaxWidth())}
    )
}