package agh.ryszard.blazej.heartbeat_app.ui.screens

import agh.ryszard.blazej.heartbeat_app.HeartbeatScreen
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor
import agh.ryszard.blazej.heartbeat_app.ui.elements.BtDeviceCard
import agh.ryszard.blazej.heartbeat_app.ui.elements.CornerDecoration
import agh.ryszard.blazej.heartbeat_app.viewmodel.ScanViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun SensorSelectionScreen(
    navController: NavHostController,
    scanViewModel: ScanViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var foundSensors by remember { mutableStateOf(listOf<BtSensor>()) }

    LaunchedEffect(Unit) {
        foundSensors = scanViewModel.findSensors()
    }
    Scaffold (containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CornerDecoration(
                modifier = Modifier
                    .align(Alignment.TopStart)
            )
            CornerDecoration(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .rotate(180.0f)
            )
            Column(
                modifier = Modifier
                    .padding(
                        start = 50.dp,
                        end = 50.dp,
                    )
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 20.dp),
                    text = "Select sensor to connect",
                    style = MaterialTheme.typography.titleLarge
                )
                if(foundSensors.isNotEmpty()) {
                    foundSensors.forEach { sensor ->
                        BtDeviceCard(
                            icon = Icons.Rounded.FavoriteBorder,
                            name = sensor.name,
                            extraInfo = "MAC: ${sensor.mac}",
                            onClick = {
                                coroutineScope.launch {
                                    onDeviceClick(navController, sensor, scanViewModel)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                else {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

private suspend fun onDeviceClick(navController: NavHostController, btDevice: BtSensor, viewModel: ScanViewModel) {
    viewModel.addSensor(btDevice)
    navController.navigate(HeartbeatScreen.SensorLoading.name)
}