package agh.ryszard.blazej.heartbeat_app.ui.screens

import agh.ryszard.blazej.heartbeat_app.HeartbeatScreen
import agh.ryszard.blazej.heartbeat_app.R
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.DiaryEntry
import agh.ryszard.blazej.heartbeat_app.ui.elements.AlertDialogTemplate
import agh.ryszard.blazej.heartbeat_app.ui.elements.TimePickerDialog
import agh.ryszard.blazej.heartbeat_app.ui.elements.TimepickerTextField
import agh.ryszard.blazej.heartbeat_app.viewmodel.ScanViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
@ExperimentalMaterial3Api
fun DiaryEntryScreen(navController: NavHostController, scanViewModel: ScanViewModel) {
    var label by remember { mutableStateOf("") }
    val time = rememberTimePickerState(is24Hour = true)
    var description by remember { mutableStateOf("") }
    val showTimePicker = remember { mutableStateOf(false) }
    var isTimeSelected = false
    val showDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold (containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if(showDialog.value) {
                AlertDialogTemplate(
                    onDismissRequest = {showDialog.value = false},
                    onConfirmation = { showDialog.value = false; onCancel(navController) },
                    dialogTitle = "Are you sure to cancel?",
                    dialogText = "All data will be lost"
                )
            }
            if (showTimePicker.value) {
                TimePickerDialog(onDismiss =
                {
                    showTimePicker.value = false
                    isTimeSelected = true
                },
                    timePickerState = time)
            }
            Column(
                modifier = Modifier
                    .padding(36.dp, 18.dp, 36.dp, 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adding event",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = {
                        showDialog.value = true
                    }
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "X sign")
                    }
                }
                OutlinedTextField(
                    value = label,
                    onValueChange = {label = it},
                    label = { Text("Label") },
                    leadingIcon = @Composable {
                        Icon(
                            painterResource(R.drawable.label_24px),
                            contentDescription = "Label"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                TimepickerTextField(
                    onClick = { showTimePicker.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    isTimeSelected = isTimeSelected,
                    leadingIcon = @Composable {
                        Icon(
                            painterResource(R.drawable.schedule_24px),
                            contentDescription = "Clock"
                        )
                    },
                    time = time,
                    label = "Time"
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = {description = it},
                    label = { Text("Description") },
                    minLines = 8,
                    maxLines = 20,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
            Button(
                onClick = {
                    coroutineScope.launch{
                        onSave(navController,
                            scanViewModel,
                            DiaryEntry(label, time.hour, time.minute, description)
                        )
                    }
                },
                modifier = Modifier
                    .padding(36.dp, 12.dp, 36.dp, 18.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = stringResource(R.string.save))
            }
        }
    }
}

private fun onCancel(navController: NavHostController) {
    navController.navigate(HeartbeatScreen.GatewayMenu.name)
}

private suspend fun onSave(navController: NavHostController, viewModel: ScanViewModel, entry: DiaryEntry) {
    viewModel.addEntry(entry)
    navController.navigate(HeartbeatScreen.GatewayMenu.name)
}