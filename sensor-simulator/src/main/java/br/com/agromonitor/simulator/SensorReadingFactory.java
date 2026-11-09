package br.com.agromonitor.simulator;

import java.time.Instant;
public class SensorReadingFactory {
    public SensorReading create(String id, String farm, String type, double value, Instant time) {
        String unit = switch (type) {
            case "SOIL_MOISTURE" -> "%";
            case "SOIL_PH" -> "pH";
            case "LIGHT" -> "lux";
            case "AIR_TEMPERATURE" -> "°C";
            default -> throw new IllegalArgumentException("Tipo desconhecido: " + type);
        };
        String alert = null;
        if (type.equals("SOIL_MOISTURE") && value < 25) alert = "Umidade abaixo do limite configurado";
        if (type.equals("SOIL_PH") && (value < 5.5 || value > 7.5)) alert = "pH fora da faixa demonstrativa configurada";
        String status = alert == null ? "NORMAL" : "CRITICAL";
        return new SensorReading(id, farm, type, value, unit, time, status, alert);
    }
}
