package it.gi.iot.node.model.sensors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorLoader {

    private static SensorLoader instance;
    private final Map<String, Sensor> loadedSensors = new HashMap<>();
    private final File pluggableCode = new File("plugins");
    private String TAG = "[SensorLoader] ";

    private SensorLoader() {
        if (!pluggableCode.exists()) {
            pluggableCode.mkdirs();
        }
    }

    public static synchronized SensorLoader getInstance() {
        if (instance == null) {
            instance = new SensorLoader();
        }
        return instance;
    }

    public void loadSensorsFromJson(String jsonPath, SensorEventListener listener) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<SensorDescriptor> sensors = mapper.readValue(new File(jsonPath), new TypeReference<List<SensorDescriptor>>() {});

        List<CompletableFuture<Void>> futures = sensors.stream()
                .map(desc -> CompletableFuture.runAsync(() -> {
                    try {
                        loadSensor(desc, listener);
                    } catch (Exception e) {
                        System.err.println(TAG + "Errore nel caricamento del sensore: " + desc.getName());
                        e.printStackTrace();
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void loadSensor(SensorDescriptor descriptor, SensorEventListener listener) throws Exception {
        String fileName = descriptor.getName() + "-" + descriptor.getVersion() + ".jar";
        File localJar = new File(pluggableCode, fileName);

        if (!localJar.exists()) {
            System.out.println(TAG+"Scarico JAR da remoto: " + descriptor.getUrl());
            downloadJar(descriptor.getUrl(), localJar);
        } else {
            System.out.println(TAG + "Uso JAR locale: " + localJar.getAbsolutePath());
        }

        URL[] urls = { localJar.toURI().toURL() };
        URLClassLoader cl = new URLClassLoader(urls);
        Class<?> sensorClass = cl.loadClass(descriptor.getClassName());
        Sensor sensor = (Sensor) sensorClass.getDeclaredConstructor().newInstance();
        sensor.setSensorEventListener(listener);
        sensor.initialize();

        loadedSensors.put(descriptor.getName(), sensor);
    }

    private void downloadJar(String url, File dest) throws IOException {
        try (InputStream in = new URL(url).openStream();
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public Map<String, Sensor> getAllSensors() {
        return loadedSensors;
    }

    public Sensor getSensor(String name) {
        return loadedSensors.get(name);
    }

    public void unloadSensor(String name) {
        loadedSensors.remove(name);
    }
}