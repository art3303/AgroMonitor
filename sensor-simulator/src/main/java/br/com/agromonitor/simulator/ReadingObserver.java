package br.com.agromonitor.simulator;
public interface ReadingObserver {
    void onCritical(SensorReading reading);
}
