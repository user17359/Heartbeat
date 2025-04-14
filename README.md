# Heartbeat
IoT solution using a wearable ECG signal recorder and mobile application working as the user interface. It allows for signal registration for various periods, from short to long measurements, and usage during exercise.

The general dataflow of the project is represented on the diagram:

<p  align="center">
  <img src="https://github.com/user17359/Heartbeat/blob/main/topology.png" width="70%" >
</p>

Gateway is designed as a simple device able to connect both to the server, and gather data from the sensor, the device itself is steered by a mobile application intended as its user interface.

### Mobile app

Exemplary screens from the steering app:

<p  align="center">
    <img src="https://github.com/user17359/Heartbeat/blob/main/gateway_menu.png" width="30%" >
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <img src="https://github.com/user17359/Heartbeat/blob/main/new_measurement_notdelayed.png" width="30%" >
</p>

### Examplary collected data

Comparison between solution using MAX-ECG-MONITOR sensor and Apple Watch 8 in simultaneous measurement:

<p  align="center">
  <img src="https://github.com/user17359/Heartbeat/blob/main/comparison.png" width="70%" >
</p>

Heart rate during ramp exercise using MAX-ECG-MONITOR sensor:

<p  align="center">
  <img src="https://github.com/user17359/Heartbeat/blob/main/ramp.png" width="70%" >
</p>
