package agh.ryszard.blazej.heartbeat_app.ui.screens

import agh.ryszard.blazej.heartbeat_app.HeartbeatScreen
import agh.ryszard.blazej.heartbeat_app.R
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor
import agh.ryszard.blazej.heartbeat_app.ui.elements.AlertDialogTemplate
import agh.ryszard.blazej.heartbeat_app.ui.elements.BtDeviceCard
import agh.ryszard.blazej.heartbeat_app.viewmodel.ScanViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.juul.kable.State

@Composable
fun GatewayMenuScreen(
    navController: NavHostController,
    scanViewModel: ScanViewModel
) {
    val connectionState = scanViewModel.connectionState.observeAsState()
    var rememberedSensors by remember { mutableStateOf(listOf<BtSensor>()) }

    LaunchedEffect(connectionState.value) {
        if(connectionState.value is State.Disconnected) {
            navController.navigate(HeartbeatScreen.GatewaySelection.name)
        }
        else {
            rememberedSensors = scanViewModel.getSensors()
        }
    }

    val showDialog = remember { mutableStateOf(false) }
    Scaffold (containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if(showDialog.value) {
                AlertDialogTemplate(
                    onDismissRequest = {showDialog.value = false},
                    onConfirmation = {onDisconnect(scanViewModel); showDialog.value = false},
                    dialogTitle = "Are you sure to disconnect?",
                    dialogText = "lorem ipsum sit dolar amet"
                )
            }
            Column(
                modifier = Modifier
                    .padding(36.dp, 18.dp, 36.dp, 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scanViewModel.peripheral?.name ?: "Gateway",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    IconButton(
                        onClick = {
                            showDialog.value = true
                        }
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "X mark")
                    }
                }
                HorizontalDivider()
                Text(
                    text = "Remembered sensors",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 12.dp),
                )
                rememberedSensors.forEach { sensor ->
                    val settings = scanViewModel.getSettings(sensor)
                    BtDeviceCard(
                        icon = painterResource(settings.icon),
                        name = sensor.name,
                        extraInfo = sensor.details,
                        onClick = { onSensorClick(navController, sensor.mac) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            Row (
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(36.dp, 12.dp, 36.dp, 18.dp)
            ){
                OutlinedButton(
                    onClick = { onAddEvent(navController) },
                    modifier = Modifier
                        .weight(1.5f)
                ) {
                    Text(text = stringResource(R.string.add_event))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { onAddSensor(navController) },
                    modifier = Modifier
                        .weight(2.0f)
                ) {
                    Text(text = stringResource(R.string.add_sensor))
                }
            }
        }
    }
}

private fun onDisconnect(scanViewModel: ScanViewModel) {
    scanViewModel.disconnectLePeripheral()
}
private fun onSensorClick(navController: NavHostController, mac: String) {
    navController.navigate("${HeartbeatScreen.NewMeasurement.name}/$mac")
}
private fun onAddEvent(navController: NavHostController) {
    navController.navigate(HeartbeatScreen.DiaryEntry.name)
}
private fun onAddSensor(navController: NavHostController) {
    navController.navigate(HeartbeatScreen.SensorSelection.name)
}