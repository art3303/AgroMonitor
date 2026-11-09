package br.com.agromonitor.simulator;
public class ConsoleAlertObserver implements ReadingObserver {
    @Override
    public void onCritical(SensorReading r) {
        System.out.printf("[EDGE ALERT] %s: %.2f %s — %s%n", r.sensorId(), r.value(), r.unit(), r.edgeAlert());
    }
}
