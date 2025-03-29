package it.gi.iot.node.model.sensors;

import lombok.Data;

@Data
public class SensorDescriptor {
    private String name;
    private String className;
    private String version;
    private String url;
}
