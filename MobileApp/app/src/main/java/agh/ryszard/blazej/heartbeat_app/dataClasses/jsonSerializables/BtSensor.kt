package agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables

import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.TypeTag
import kotlinx.serialization.Serializable

@Serializable
data class BtSensor(
    val name: String = "",
    val mac: String = "",
    val tag: TypeTag = TypeTag.OTHER,
    val details: String = ""
)