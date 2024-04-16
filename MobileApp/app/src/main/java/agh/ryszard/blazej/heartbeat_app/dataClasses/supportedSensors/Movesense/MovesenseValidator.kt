package agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.Movesense

import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor

fun movesenseValidator(device: BtSensor): Boolean{
    val regex = Regex("Movesense")
    return regex.containsMatchIn(device.name)
}