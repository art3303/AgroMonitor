package br.com.agromonitor.processing;

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
    public void setId(String v) {
        id = v;
    }
    public String getSensorId() {
        return sensorId;
    }
    public void setSensorId(String v) {
        sensorId = v;
    }
    public String getFarmId() {
        return farmId;
    }
    public void setFarmId(String v) {
        farmId = v;
    }
    public String getType() {
        return type;
    }
    public void setType(String v) {
        type = v;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String v) {
        unit = v;
    }
    public String getEdgeStatus() {
        return edgeStatus;
    }
    public void setEdgeStatus(String v) {
        edgeStatus = v;
    }
    public String getEdgeAlert() {
        return edgeAlert;
    }
    public void setEdgeAlert(String v) {
        edgeAlert = v;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double v) {
        value = v;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant v) {
        timestamp = v;
    }
}
