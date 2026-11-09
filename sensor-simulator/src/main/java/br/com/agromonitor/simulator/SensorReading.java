package br.com.agromonitor.simulator;

import java.time.Instant;
import java.util.Locale;
public record SensorReading(String sensorId, String farmId, String type, double value, String unit, Instant timestamp, String edgeStatus, String edgeAlert) {
    public boolean isCritical() {
        return "CRITICAL".equals(edgeStatus);
    }
    public String toJson() {
        return String.format(Locale.US, "{\"sensorId\":\"%s\",\"farmId\":\"%s\",\"type\":\"%s\",\"value\":%.3f,\"unit\":\"%s\",\"timestamp\":\"%s\",\"edgeStatus\":\"%s\",\"edgeAlert\":%s}", sensorId, farmId, type, value, unit, timestamp, edgeStatus, edgeAlert == null ? "null" : "\"" + edgeAlert + "\"");
    }
}
