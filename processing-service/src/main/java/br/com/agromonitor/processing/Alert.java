package br.com.agromonitor.processing;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document("alerts")
public class Alert {
    @Id
    private String id;
    private String sensorId, farmId, type, message, severity;
    private double value;
    private Instant timestamp;
    public Alert() {
    }
    public Alert(Reading r) {
        sensorId = r.getSensorId();
        farmId = r.getFarmId();
        type = r.getType();
        message = r.getEdgeAlert() != null ? r.getEdgeAlert() : "Leitura crítica identificada";
        severity = "CRITICAL";
        value = r.getValue();
        timestamp(r.getTimestamp());
    }
    private void timestamp(Instant t) {
        timestamp = t;
    }
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
    public String getMessage() {
        return message;
    }
    public String getSeverity() {
        return severity;
    }
    public double getValue() {
        return value;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
}
