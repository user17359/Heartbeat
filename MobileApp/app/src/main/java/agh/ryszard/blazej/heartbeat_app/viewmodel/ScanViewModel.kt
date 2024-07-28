package agh.ryszard.blazej.heartbeat_app.viewmodel

import agh.ryszard.blazej.heartbeat_app.data.DeviceRepository
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.DiaryEntry
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.Measurement
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SensorSettings
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SupportedSensors
import agh.ryszard.blazej.heartbeat_app.utils.peripheralScope
import android.bluetooth.le.ScanSettings
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juul.kable.AndroidAdvertisement
import com.juul.kable.ConnectionLostException
import com.juul.kable.Filter
import com.juul.kable.ObsoleteKableApi
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.Transport
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Time after we stop scanning
//private const val SCAN_PERIOD: Long = 60000

class ScanViewModel(private val deviceRepository: DeviceRepository = DeviceRepository()): ViewModel() {

    private val sensorServiceUUID = "a56f5e06-fd24-4ffe-906f-f82e916262bc"
    private val eventServiceUUID = "4f8ef7bf-fe20-437b-9320-89e6108c82e0"
    private val connectivityServiceUUID = "6672b3e6-477e-4e52-a3fb-a440c57dc857"

    private val supportedSensors = SupportedSensors()

    val listOfDevices = MutableLiveData<Set<AndroidAdvertisement>>()
    val connectionState = MutableLiveData<State>()
    val connectionFailure = MutableLiveData(false)
    val reconnectState = MutableLiveData(false)

    private val _foundDevices = mutableListOf<String>()
    var peripheral: Peripheral? = null

    val startConnectionTries = 5

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    private val _scope = CoroutineScope(peripheralScope.coroutineContext + Job(peripheralScope.coroutineContext.job) + coroutineExceptionHandler)

    init {
        listOfDevices.value = setOf()
    }

    @OptIn(ObsoleteKableApi::class)
    suspend fun scanLeDevice() {
        Scanner {
            filters = listOf(
                Filter.NamePrefix("Heartbeat")
            )
            logging {
                engine = SystemLogEngine
                level = Logging.Level.Warnings
                format = Logging.Format.Multiline
            }
            scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
        }.advertisements.collect{ result ->
            listOfDevices.postValue(listOfDevices.value?.toMutableList()?.apply {
                if(!_foundDevices.contains(result.address)) {
                    add(result)
                    _foundDevices.add(result.address)
                }
            }?.toSet())
        }
    }

    fun connectLePeripheral(advertisement: AndroidAdvertisement) {
        connectionFailure.postValue(false)
        listOfDevices.value = setOf()
        _foundDevices.clear()
        val peripheral = _scope.peripheral(advertisement) {
            transport = Transport.Le
        }
        this.peripheral = peripheral
        _scope.launch {
            asyncConnection(startConnectionTries)
        }
    }

    fun disconnectLePeripheral() {
        val disconnectScope = CoroutineScope(peripheralScope.coroutineContext + Job(peripheralScope.coroutineContext.job) + coroutineExceptionHandler)
        disconnectScope.launch {
            asyncDisconnection()
        }
    }

    private suspend fun asyncConnection(leftTries: Int) {
        try {
            var connected = false
            while (!connected) {
                peripheral!!.connect()
                peripheral!!.state.collect { state ->
                    connectionState.postValue(state)
                    if (state is State.Connected) {
                        connected = true
                    }
                }
            }
        }
        catch (e: ConnectionLostException){
            if(leftTries > 0){
                asyncConnection(leftTries - 1)
            }
            else {
                connectionFailure.postValue(true)
            }
        }
    }

    private suspend fun asyncDisconnection() {
        peripheral!!.disconnect()
        peripheral!!.state.collect { state ->
            connectionState.postValue(state)
        }
    }

    suspend fun getSensors(): List<BtSensor> {
        val characteristic = characteristicOf(
            service = connectivityServiceUUID,
            characteristic = "9f03f5db-93ba-402b-951f-1c8e008b5adc",
        )

        reconnectState.postValue(false)

        val reading = peripheral!!.read(characteristic).decodeToString()
        val sensors: List<BtSensor> = Json.decodeFromString(reading)
        sensors.forEach{ sensor ->
            supportedSensors.settingsList.forEach { sensorType ->
                if(sensorType.sensorValidator(sensor)) {
                    val settings =
                        supportedSensors.settingsList.first { it.sensorValidator(sensor) }
                    val device = BtSensor(sensor.name, sensor.mac, settings.tag, sensor.details)
                    if(!deviceRepository.checkExistence(device)){
                        deviceRepository.addDevice(device)
                    }
                }
            }
        }
        return deviceRepository.deviceList
    }

    suspend fun findSensors(): List<BtSensor> {
        val characteristic = characteristicOf(
            service = connectivityServiceUUID,
            characteristic = "5fc4077d-e88f-4b5d-956b-955d30ec5899",
        )
        val reading = peripheral!!.read(characteristic).decodeToString()
        val sensors: List<BtSensor> = Json.decodeFromString(reading)
        val filteredSensors = mutableListOf<BtSensor>()
        sensors.forEach{ sensor ->
            if(!deviceRepository.deviceList.any { it.mac == sensor.mac }){
                try {
                    val settings =
                        supportedSensors.settingsList.first { it.sensorValidator(sensor) }
                    filteredSensors.add(BtSensor(sensor.name, sensor.mac, settings.tag, sensor.details))
                }
                catch (_: NoSuchElementException) {

                }
            }
        }
        return filteredSensors
    }

    suspend fun addSensor(sensor: BtSensor) {
        val characteristic = characteristicOf(
            service = connectivityServiceUUID,
            characteristic = "1fe83b02-0788-4af7-9a69-af6b9e9782a7",
        )
        deviceRepository.addDevice(sensor)
        val jsonString = Json.encodeToString(sensor)
        peripheral!!.write(characteristic, jsonString.toByteArray(Charsets.UTF_8))
    }

    suspend fun addMeasurement(measurement: Measurement): Boolean{
        val characteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "18c7e933-73cf-4d47-9973-51a53f0fec4e",
        )

        val correctedMeasurement = Measurement(
            measurement.mac,
            measurement.type,
            measurement.label,
            measurement.startMilliseconds,
            measurement.endMilliseconds,
            measurement.sensors,
        )

        val jsonString = Json.encodeToString(correctedMeasurement)
        peripheral!!.write(characteristic, jsonString.toByteArray(Charsets.UTF_8))
        return true
    }
    
    // for multi-connection workaround
    suspend fun timedReconnect(timeMilis: Long) {
        peripheral!!.disconnect()
        delay(timeMilis)
        reconnectState.postValue(true)
        asyncConnection(5)
    }

    suspend fun endMeasurement(device: BtSensor){
        val readCharacteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "1fbbda31-a97a-4d1d-a4dd-a7c17b853dcd"
        )
        val writeCharacteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "2fd2ac39-1f6b-4d55-aa2b-3dd049420235"
        )

        peripheral!!.write(writeCharacteristic, device.mac.toByteArray(Charsets.UTF_8))
        peripheral!!.read(readCharacteristic)
    }

    suspend fun addEntry(entry: DiaryEntry) {
        val characteristic = characteristicOf(
            service = eventServiceUUID,
            characteristic = "27e571d9-53fa-4756-88da-07716d7ea633",
        )
        val jsonString = Json.encodeToString(entry)
        peripheral!!.write(characteristic, jsonString.toByteArray(Charsets.UTF_8))
    }

    fun getDevice(mac: String): BtSensor{
        return deviceRepository.deviceList.first { it.mac == mac }
    }

    fun getSettings(device: BtSensor): SensorSettings{
        return supportedSensors.fromTag(device.tag)
    }
}
