package agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables

import kotlinx.serialization.Serializable

@Serializable
data class Measurement (
    val mac: String,
    val type: String,
    val label: String,

    // Start of measurement date
    val startMilliseconds: Long,

    // End of measurement date
    val endMilliseconds: Long,

    val sensors: List<HashMap<String, String>>,
)