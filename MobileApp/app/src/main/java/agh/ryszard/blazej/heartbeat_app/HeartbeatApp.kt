package agh.ryszard.blazej.heartbeat_app

import agh.ryszard.blazej.heartbeat_app.ui.screens.DiaryEntryScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.GatewayLoadingScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.GatewayMenuScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.GatewaySelectionScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.MeasurementLoadingScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.NewMeasurementScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.SensorLoadingScreen
import agh.ryszard.blazej.heartbeat_app.ui.screens.SensorSelectionScreen
import agh.ryszard.blazej.heartbeat_app.viewmodel.ScanViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class HeartbeatScreen {
    GatewaySelection,
    GatewayLoading,
    GatewayMenu,
    SensorSelection,
    SensorLoading,
    DiaryEntry,
    NewMeasurement,
    MeasurementLoading
}

@Composable
@ExperimentalMaterial3Api
fun HeartbeatApp(
    navController: NavHostController = rememberNavController()
) {

    val viewModel: ScanViewModel = viewModel()

    NavHost(navController = navController, startDestination = HeartbeatScreen.GatewaySelection.name) {
        composable(HeartbeatScreen.GatewaySelection.name) {
            GatewaySelectionScreen(navController, viewModel)
        }
        composable(HeartbeatScreen.GatewayLoading.name) { _ ->
            GatewayLoadingScreen(navController, viewModel)
        }
        composable(HeartbeatScreen.GatewayMenu.name) { _ ->
            GatewayMenuScreen(navController, viewModel)
        }
        composable(HeartbeatScreen.SensorSelection.name) {
            SensorSelectionScreen(navController, viewModel)
        }
        composable(HeartbeatScreen.SensorLoading.name) {
            SensorLoadingScreen(navController)
        }
        composable(HeartbeatScreen.DiaryEntry.name) {
            DiaryEntryScreen(navController, viewModel)
        }
        composable(HeartbeatScreen.NewMeasurement.name + "/{mac}") {
            val mac = it.arguments?.getString("mac")
            NewMeasurementScreen(navController, viewModel, mac ?: "")
        }
        composable(HeartbeatScreen.MeasurementLoading.name + "/{mac}") {
            val mac = it.arguments?.getString("mac")
            MeasurementLoadingScreen(navController, viewModel, mac ?: "")
        }
    }
}