package agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors

import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.Movesense.MovesenseSettings

enum class TypeTag(val encodedName: String){
    MOVESENSE("Movesense"),
    OTHER("Other")
}
data class SupportedSensors (
    val settingsList: List<SensorSettings> = listOf(
        MovesenseSettings(tag = TypeTag.MOVESENSE)
    )
){
    fun fromTag(tag: TypeTag): SensorSettings{
        return settingsList.first {
            it.tag == tag
        }
    }
}