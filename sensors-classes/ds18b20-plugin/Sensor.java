package it.gi.iot.node.model.sensors;

public interface Sensor {
    void initialize();                             // Inizializzazione del sensore
    double readData();                             // Lettura dati (es: temperatura)
    void turnOn();                                 // Attivazione
    void turnOff();                                // Spegnimento
    void reset();                                  // Reset interno
    long getReadingIntervalMillis();               // Frequenza di lettura (millisecondi)
    void setSensorEventListener(SensorEventListener listener); // Listener eventi
}

