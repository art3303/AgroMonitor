package br.com.agromonitor.api;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("readings")
public class Reading {
    @Id
    private String id;
    private String sensorId, farmId, type, unit, edgeStatus, edgeAlert;
    private double value;
    private Instant timestamp;
    public String getId() {
        return id;
    }
    public String getSensorId() {
        return sensorId;
    }
    public String getFarmId() {
        return farmId;
    }
    public String getType() {
        return type;
    }
    public String getUnit() {
        return unit;
    }
    public String getEdgeStatus() {
        return edgeStatus;
    }
    public String getEdgeAlert() {
        return edgeAlert;
    }
    public double getValue() {
        return value;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
}
