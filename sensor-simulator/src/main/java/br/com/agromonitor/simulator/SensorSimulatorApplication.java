package br.com.agromonitor.simulator;

import org.eclipse.paho.client.mqttv3.*;
import java.time.Instant;
import java.util.Random;
public class SensorSimulatorApplication {
    public static void main(String[] args) throws Exception {
        String broker = env("MQTT_BROKER", "tcp://localhost:1883");
        long interval = Long.parseLong(env("SIMULATOR_INTERVAL_MS", "3000"));
        MqttClient client = new MqttClient(broker, "agro-simulator-" + MqttClient.generateClientId());
        int attempts = 0;
        while (!client.isConnected()) {
            try {
                client.connect();
            }
            catch (MqttException ex) {
                if (++attempts >= 20) throw ex;
                System.err.println("Broker ainda indisponível; nova tentativa em 3s: " + ex.getMessage());
                Thread.sleep(3000);
            }
        }
        Random random = new Random();
        SensorReadingFactory factory = new SensorReadingFactory();
        ReadingObserver observer = new ConsoleAlertObserver();
        String[] ids = {
            "sensor-001", "sensor-002", "sensor-003", "sensor-004"
        };
        String[] types = {
            "SOIL_MOISTURE", "SOIL_PH", "LIGHT", "AIR_TEMPERATURE"
        };
        System.out.println("AgroMonitor simulator conectado ao broker " + broker);
        while (!Thread.currentThread().isInterrupted()) {
            for (int i = 0; i < ids.length; i++) {
                double value = switch (types[i]) {
                    case "SOIL_MOISTURE" -> 10 + random.nextDouble() * 75;
                    case "SOIL_PH" -> 4.5 + random.nextDouble() * 4;
                    case "LIGHT" -> random.nextDouble() * 1000;
                    default -> 15 + random.nextDouble() * 25;
                };
                SensorReading reading = factory.create(ids[i], "fazenda-demo", types[i], value, Instant.now());
                if (reading.isCritical()) observer.onCritical(reading);
                String json = reading.toJson();
                MqttMessage message = new MqttMessage(json.getBytes());
                message.setQos(1);
                client.publish("agro/" + ids[i] + "/readings", message);
            }
            Thread.sleep(interval);
        }
    }
    private static String env(String key, String fallback) {
        return System.getenv().getOrDefault(key, fallback);
    }
}
