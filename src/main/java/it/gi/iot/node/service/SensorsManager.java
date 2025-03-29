package it.gi.iot.node.service;

import it.gi.iot.node.model.sensors.Sensor;
import it.gi.iot.node.model.sensors.SensorEventListener;
import it.gi.iot.node.model.sensors.SensorLoader;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SensorsManager implements SensorEventListener {

    private final SensorLoader loader;
    private final File dataDir = new File("data");
    private final File plugins = new File("plugins");
    private final File conf = new File("conf");

    public SensorsManager() {
        this.loader = SensorLoader.getInstance();

        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        if (!plugins.exists()) {
            plugins.mkdirs();
        }

        if (!conf.exists()) {
            conf.mkdirs();
        }
    }

    @PostConstruct
    public void init() {
        try {

            loader.loadSensorsFromJson("conf/sensors.json", this); // load sensors list.. can be improved.. json is not easly readable by non thechnical installers
            startAllSensors();
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento dei sensori: " + e.getMessage());
        }
    }

    public void startAllSensors() {
        loader.getAllSensors().values().stream()
                .peek(sensor -> sensor.setSensorEventListener(this))
                .forEach(Sensor::turnOn);
    }

    public void stopAllSensors() {
        loader.getAllSensors().values().forEach(Sensor::turnOff);
    }

    @Override
    public void onSensorDataReady(String sensorName, double data) {
        System.out.println("Dati ricevuti dal sensore [" + sensorName + "]: " + data);
    }
}

