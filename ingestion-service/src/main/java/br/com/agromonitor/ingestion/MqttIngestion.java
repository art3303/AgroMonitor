package br.com.agromonitor.ingestion;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MqttIngestion implements CommandLineRunner {
    @Value("${MQTT_BROKER:tcp://localhost:1883}")
    String broker;
    @Value("${PROCESSING_URL:http://localhost:8082}")
    String processingUrl;
    @Override
    public void run(String... args) throws Exception {
        MqttClient client = new MqttClient(broker, "agro-ingestion-" + MqttClient.generateClientId());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        int attempts = 0;
        while (!client.isConnected()) {
            try {
                client.connect(options);
            }
            catch (MqttException ex) {
                if (++attempts >= 20) throw ex;
                System.err.println("Broker ainda indisponível; nova tentativa em 3s: " + ex.getMessage());
                Thread.sleep(3000);
            }
        }
        client.subscribe("agro/+/readings", 1, (topic, message) -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                new RestTemplate().postForEntity(processingUrl + "/internal/readings", new HttpEntity<>(new String(message.getPayload()), headers), String.class);
                System.out.println("Leitura encaminhada: " + topic);
            }
            catch (Exception ex) {
                System.err.println("Falha ao encaminhar leitura: " + ex.getMessage());
            }
        }
        );
        System.out.println("Ingestion inscrito em agro/+/readings");
    }
}
