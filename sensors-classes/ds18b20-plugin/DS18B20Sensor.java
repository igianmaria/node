package it.gi.iot.node.model.sensors.temp;

import it.gi.iot.node.model.sensors.Sensor;
import it.gi.iot.node.model.sensors.SensorEventListener;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DS18B20Sensor implements Sensor {

    private SensorEventListener listener;
    private Timer timer;
    private final long intervalMillis = 5000; // 5 secondi tra una lettura e l'altra
    private final Random random = new Random();

    @Override
    public void initialize() {
        System.out.println("[DS18B20] Inizializzazione completata.");
        timer = new Timer();
    }

    @Override
    public double readData() {
        // Simulazione lettura temperatura tra 20°C e 25°C
        return 20 + (5 * random.nextDouble());
    }

    @Override
    public void turnOn() {
        System.out.println("[DS18B20] Sensore acceso.");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                double temperature = readData();
                if (listener != null) {
                    listener.onSensorDataReady("DS18B20", temperature);
                }
            }
        }, 0, intervalMillis);
    }

    @Override
    public void turnOff() {
        System.out.println("[DS18B20] Sensore spento.");
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void reset() {
        System.out.println("[DS18B20] Resetting sensore.");
    }

    @Override
    public long getReadingIntervalMillis() {
        return intervalMillis;
    }

    @Override
    public void setSensorEventListener(SensorEventListener listener) {
        this.listener = listener;
    }

    // Metodo extra per accedere all'ultima temperatura simulata (opzionale)
    public double getLastMeasuredTemperature() {
        return readData();
    }
}
