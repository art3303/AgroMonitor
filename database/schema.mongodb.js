// AgroMonitor IoT - esquema do banco MongoDB
// Banco: agromonitor
// Execute com mongosh: mongosh "mongodb://localhost:27017/agromonitor" schema.mongodb.js
// No Docker Compose do projeto: docker compose exec -T mongo mongosh agromonitor < database/schema.mongodb.js
const database = db.getSiblingDB("agromonitor");
// Coleção de leituras recebidas dos sensores.
const readingsValidator = {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Leituras dos sensores",
            required: ["sensorId", "farmId", "type", "value", "timestamp"],
            properties: {
                _id: { bsonType: ["objectId", "string"], description: "Identificador único da leitura" },
                sensorId: { bsonType: "string", description: "Identificador do sensor" },
                farmId: { bsonType: "string", description: "Identificador da propriedade rural" },
                type: {
                    bsonType: "string",
                    enum: ["SOIL_MOISTURE", "SOIL_PH", "LIGHT", "AIR_TEMPERATURE"],
                    description: "Tipo de medição realizada"
                },
                unit: { bsonType: "string", description: "Unidade da medição, por exemplo %, pH, lux ou °C" },
                value: { bsonType: ["double", "int", "long", "decimal"], description: "Valor medido pelo sensor" },
                timestamp: { bsonType: "date", description: "Data e hora da leitura" },
                edgeStatus: { bsonType: "string", description: "Resultado da análise local no Edge" },
                edgeAlert: { bsonType: "string", description: "Descrição do alerta identificado no Edge, quando houver" }
            }
        }
    },
    validationLevel: "strict",
    validationAction: "error"
};
// Coleção de alertas gerados a partir de leituras críticas.
const alertsValidator = {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Alertas do monitoramento agrícola",
            required: ["sensorId", "farmId", "type", "message", "severity", "value", "timestamp"],
            properties: {
                _id: { bsonType: ["objectId", "string"], description: "Identificador único do alerta" },
                sensorId: { bsonType: "string", description: "Sensor que originou o alerta" },
                farmId: { bsonType: "string", description: "Propriedade rural relacionada ao alerta" },
                type: {
                    bsonType: "string",
                    enum: ["SOIL_MOISTURE", "SOIL_PH", "LIGHT", "AIR_TEMPERATURE"],
                    description: "Tipo de medição que originou o alerta"
                },
                message: { bsonType: "string", description: "Descrição do problema identificado" },
                severity: {
                    bsonType: "string",
                    enum: ["LOW", "MEDIUM", "HIGH", "CRITICAL"],
                    description: "Nível de severidade do alerta"
                },
                value: { bsonType: ["double", "int", "long", "decimal"], description: "Valor medido que gerou o alerta" },
                timestamp: { bsonType: "date", description: "Data e hora em que a leitura crítica ocorreu" }
            }
        }
    },
    validationLevel: "strict",
    validationAction: "error"
};
// Cria as coleções quando ainda não existem ou atualiza a validação quando já existem.
function ensureCollection(name, options) {
    if (database.getCollectionNames().includes(name)) {
        database.runCommand({ collMod: name, ...options });
    }
    else {
        database.createCollection(name, options);
    }
}
ensureCollection("readings", readingsValidator);
ensureCollection("alerts", alertsValidator);
// Índices para acelerar consultas do painel e do histórico por sensor/propriedade.
database.readings.createIndex({ sensorId: 1, timestamp: -1 }, { name: "idx_readings_sensor_timestamp" });
database.readings.createIndex({ farmId: 1, timestamp: -1 }, { name: "idx_readings_farm_timestamp" });
database.alerts.createIndex({ farmId: 1, timestamp: -1 }, { name: "idx_alerts_farm_timestamp" });
database.alerts.createIndex({ severity: 1, timestamp: -1 }, { name: "idx_alerts_severity_timestamp" });
print("Esquema do AgroMonitor IoT criado/validado no banco 'agromonitor'.");
