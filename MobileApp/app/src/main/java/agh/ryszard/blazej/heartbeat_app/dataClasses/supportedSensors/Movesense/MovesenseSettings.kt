package agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.Movesense

import agh.ryszard.blazej.heartbeat_app.R
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SensorSettings
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.TypeTag
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SensorToggleParameter
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SensorUnit

data class MovesenseSettings (
    override val units: List<SensorUnit> = listOf(
        SensorUnit(
            name = "ECG",
            encodedName = "ecg",
            parameters = listOf(
                SensorToggleParameter(
                    name = "Probing frequency [Hz]",
                    encodedName = "probing",
                    options = listOf(
                        "100",
                        "125",
                        //"200",
                        "250",
                        //"256",
                        "500",
                        //"512"
                    ),
                    uniqueId = 0
                ),
            )
        ),
        SensorUnit(
            name = "IMU",
            encodedName = "imu",
            parameters = listOf(
                SensorToggleParameter(
                    name = "Probing frequency [Hz]",
                    encodedName = "probing",
                    options = listOf(
                        "208",
                        "104",
                        "52",
                        "26"
                    ),
                    uniqueId = 1
                ),
            )
        ),
        SensorUnit(
            name = "Heart rate",
            encodedName = "hr",
            parameters = listOf(
            )
        )
    ),
    override val tag: TypeTag
) : SensorSettings(units, tag, ::movesenseValidator, R.drawable.ecg_heart_24px)