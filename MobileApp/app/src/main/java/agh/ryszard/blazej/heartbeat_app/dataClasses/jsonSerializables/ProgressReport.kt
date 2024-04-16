package agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables

import kotlinx.serialization.Serializable

@Serializable
data class ProgressReport (
    val state: String,
    val info: String
)