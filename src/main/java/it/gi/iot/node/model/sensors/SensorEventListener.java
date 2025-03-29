package it.gi.iot.node.model.sensors;

public interface SensorEventListener {
    void onSensorDataReady(String sensorName, double value);
}

