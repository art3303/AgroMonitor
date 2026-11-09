package br.com.agromonitor.api;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final ReadingRepository readings;
    private final AlertRepository alerts;
    public ApiController(ReadingRepository r, AlertRepository a) {
        readings = r;
        alerts = a;
    }
    @GetMapping("/readings")
    public List<Reading> readings(
        @RequestParam(defaultValue="100")
    int limit) {
        return readings.findTop500ByOrderByTimestampDesc().stream().limit(Math.max(1, Math.min(limit, 500))).toList();
    }
    @GetMapping("/alerts")
    public List<Alert> alerts(
        @RequestParam(defaultValue="100")
    int limit) {
        return alerts.findTop500ByOrderByTimestampDesc().stream().limit(Math.max(1, Math.min(limit, 500))).toList();
    }
    @GetMapping("/sensors")
    public List<Map<String, Object>> sensors() {
        Map<String, Reading> latest = new LinkedHashMap<>();
        for (Reading r : readings.findTop500ByOrderByTimestampDesc()) latest.putIfAbsent(r.getSensorId(), r);
        return latest.values().stream().map(r -> Map.<String, Object>of("sensorId", r.getSensorId(), "farmId", r.getFarmId(), "type", r.getType(), "value", r.getValue(), "unit", r.getUnit(), "status", r.getEdgeStatus() == null ? "NORMAL" : r.getEdgeStatus(), "timestamp", r.getTimestamp())).collect(Collectors.toList());
    }
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        List<Reading> list = readings.findTop500ByOrderByTimestampDesc();
        long critical = list.stream().filter(r -> "CRITICAL".equals(r.getEdgeStatus())).count();
        return Map.of("totalReadings", list.size(), "activeSensors", list.stream().map(Reading::getSensorId).distinct().count(), "criticalReadings", critical, "latestReadings", list.stream().limit(12).toList(), "latestAlerts", alerts.findTop500ByOrderByTimestampDesc().stream().limit(10).toList());
    }
}
