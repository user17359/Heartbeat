package agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors

import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor

enum class ParameterType {
    Toggle
}
open class SensorSettings (
    open val units: List<SensorUnit>,
    open val tag: TypeTag,
    val sensorValidator: (BtSensor) -> Boolean,
    val icon: Int
)

// TODO: clean up visualization parameters
class SensorUnit (
    val name: String,
    val encodedName: String,
    val parameters: List<SensorParameter>,
)
open class SensorParameter (
    open val name: String,
    open val encodedName: String,
    val type: ParameterType,
    open val uniqueId: Int
)

data class SensorToggleParameter (
    val options: List<String>,
    override val name: String,
    override val uniqueId: Int,
    override val encodedName: String
): SensorParameter(name, encodedName, ParameterType.Toggle, uniqueId)