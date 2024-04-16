package agh.ryszard.blazej.heartbeat_app.data

import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor

class DeviceRepository {

    val deviceList = mutableListOf<BtSensor>()

    fun addDevice(device: BtSensor) {
        deviceList.add(device)
    }

    fun checkExistence(device: BtSensor): Boolean {
        return deviceList.any {it.mac == device.mac}
    }
}