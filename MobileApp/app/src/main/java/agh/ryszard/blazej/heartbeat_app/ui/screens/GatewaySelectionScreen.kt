package agh.ryszard.blazej.heartbeat_app.ui.screens

import agh.ryszard.blazej.heartbeat_app.HeartbeatScreen
import agh.ryszard.blazej.heartbeat_app.ui.elements.BtDeviceCard
import agh.ryszard.blazej.heartbeat_app.ui.elements.CornerDecoration
import agh.ryszard.blazej.heartbeat_app.viewmodel.ScanViewModel
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.juul.kable.AndroidAdvertisement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION)
} else {
    listOf()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GatewaySelectionScreen(
    navController: NavHostController,
    scanViewModel: ScanViewModel,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val snackbar = scanViewModel.connectionFailure.observeAsState()

    val context = LocalContext.current
    val bluetoothManager: BluetoothManager? = getSystemService(context, BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager!!.adapter


    val multiplePermissionsState = rememberMultiplePermissionsState(permissions)
    if(bluetoothAdapter!= null && bluetoothAdapter.isEnabled && multiplePermissionsState.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                scanViewModel.scanLeDevice()
            }
        }
        val listOfDevices = scanViewModel.listOfDevices.observeAsState()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = MaterialTheme.colorScheme.surface
        ) { innerPadding ->
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
                        text = "Select gateway to connect",
                        style = MaterialTheme.typography.titleLarge
                    )
                    if(listOfDevices.value?.isNotEmpty() == true) {
                        listOfDevices.value?.forEach { gateway ->
                            BtDeviceCard(
                                icon = Icons.Rounded.FavoriteBorder,
                                name = gateway.name ?: "null",
                                extraInfo = "MAC: ${gateway.address}",
                                onClick = { onDeviceClick(navController, gateway, scanViewModel) }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    else
                    {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        LaunchedEffect(Unit) {
            if (snackbar.value!!) {
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to connect")
                }
            }
        }
    }
    else if(bluetoothAdapter == null){
        Scaffold(containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
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
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Your device does not support bluetooth")
            }
        }
    }
    else if(!bluetoothAdapter.isEnabled){
        Scaffold(containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
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
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(text = "Turn on your bluetooth and restart app")
            }
        }
    }
    else {
        Scaffold(containerColor = MaterialTheme.colorScheme.surface) { innerPadding ->
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
            }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(text = "Permissions are not granted")
                    Button(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                        Text("Request permissions")
                    }
                }
            }
    }
}

private fun onDeviceClick(navController: NavHostController,
                          advertisement: AndroidAdvertisement,
                          scanViewModel: ScanViewModel) {
    scanViewModel.connectLePeripheral(advertisement)
    navController.navigate(HeartbeatScreen.GatewayLoading.name)
}