package agh.ryszard.blazej.heartbeat_app.viewmodel

import agh.ryszard.blazej.heartbeat_app.data.DeviceRepository
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.BtSensor
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.DiaryEntry
import agh.ryszard.blazej.heartbeat_app.dataClasses.jsonSerializables.Measurement
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SensorSettings
import agh.ryszard.blazej.heartbeat_app.dataClasses.supportedSensors.SupportedSensors
import agh.ryszard.blazej.heartbeat_app.utils.peripheralScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.juul.kable.AndroidAdvertisement
import com.juul.kable.ConnectionLostException
import com.juul.kable.Filter
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.characteristicOf
import com.juul.kable.logs.Logging
import com.juul.kable.logs.SystemLogEngine
import com.juul.kable.peripheral
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
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
    private val _connectionTimeoutMilis: Long = 10000
    var peripheral: Peripheral? = null

    private val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        throwable.printStackTrace()
    }

    private val _scope = CoroutineScope(peripheralScope.coroutineContext + Job(peripheralScope.coroutineContext.job) + coroutineExceptionHandler)

    init {
        listOfDevices.value = setOf()
    }

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

        }
        this.peripheral = peripheral
        val job = _scope.launch {
            asyncConnection()
        }
        _scope.launch {
            //timeout(_connectionTimeoutMilis, job)
        }
    }

    fun disconnectLePeripheral() {
        val disconnectScope = CoroutineScope(peripheralScope.coroutineContext + Job(peripheralScope.coroutineContext.job) + coroutineExceptionHandler)
        disconnectScope.launch {
            asyncDisconnection()
        }
    }

    private suspend fun asyncConnection() {
        try {
            var connected = false
            while (!connected) {
                peripheral!!.connect()
                peripheral!!.state.collect { state ->
                    connectionState.postValue(state)
                    if (state is State.Connected) connected = true
                }
            }
        }
        catch (e: ConnectionLostException){
            connectionFailure.postValue(true)
        }
    }

    private suspend fun timeout(timeMilis: Long, job: Job) {
        delay(timeMilis)
        job.cancelAndJoin()
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
        asyncConnection()
    }

    // TODO: figure out future of those functions
    /*suspend fun checkStatus(device: BtSensor) {
        val characteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "46dff0ae-21e2-4e55-8b38-3ae249e23884",
        )
       val observation = peripheral!!.observe(characteristic)
        observation.collect{ data ->
            val progress: ProgressReport = Json.decodeFromString(data.decodeToString())
            val sensorState = SensorState.fromString(progress.state)
            measurementState.postValue(sensorState)
        }
    }*/

    /*suspend fun startStatus(device: BtSensor) {
        val readCharacteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "e946c454-6083-44d1-a726-076cecfc3744"
        )
        val writeCharacteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "2fd2ac39-1f6b-4d55-aa2b-3dd049420235"
        )

        peripheral!!.write(writeCharacteristic, device.mac.toByteArray(Charsets.UTF_8))

        val jsonData = peripheral!!.read(readCharacteristic).decodeToString()
        val decodedData = Json.decodeFromString<ProgressStatus>(jsonData)

        measurementState.postValue(SensorState.fromString(decodedData.state))
        label.postValue(decodedData.label)
        startTime.postValue(decodedData.startTime)
        _units = decodedData.units
    } */

    suspend fun endMeasurement(){
        val characteristic = characteristicOf(
            service = sensorServiceUUID,
            characteristic = "1fbbda31-a97a-4d1d-a4dd-a7c17b853dcd"
        )
        peripheral!!.read(characteristic)
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
