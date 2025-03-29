package it.gi.iot.node.model.sensors;


public interface Sensor {
    void initialize();
    double readData();
    void turnOn();
    void turnOff();
    void reset();
    long getReadingIntervalMillis();
    void setSensorEventListener(SensorEventListener listener);
}

