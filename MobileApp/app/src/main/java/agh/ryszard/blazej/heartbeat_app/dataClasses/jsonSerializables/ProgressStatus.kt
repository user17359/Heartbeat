package agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables

import kotlinx.serialization.Serializable

@Serializable
data class ProgressStatus (
    val state: String,
    val label: String,
    val startTime: String,
    val units: List<HashMap<String, String>>
)