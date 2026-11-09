# AgroMonitor IoT

Protótipo acadêmico de monitoramento agrícola distribuído. Stack: Java 21, Spring Boot, MQTT (Eclipse Paho), MongoDB, JWT, Docker Compose e dashboard web.

## Arquitetura

- `sensor-simulator`: gera leituras sintéticas; aplica uma regra Edge local e publica JSON via MQTT.
- `ingestion-service`: assina `agro/+/readings`, valida mensagens e encaminha ao serviço de processamento via HTTP.
- `processing-service`: persiste leituras e alertas no MongoDB; oferece processamento paralelo/sequencial para benchmark.
- `api-service`: consulta MongoDB, emite JWT para usuário de demonstração e serve o dashboard.
- `mosquitto`: broker MQTT local.

O processamento Edge é demonstrativo e roda junto ao simulador; não representa equipamento físico. Limites de alerta são valores configuráveis e devem ser ajustados à cultura/solo reais.

## Requisitos

- Docker Desktop + Docker Compose (recomendado para executar tudo)
- Ou Java 21 e Maven 3.9+ para executar aplicações individualmente.

## Executar com Docker

1. Copie `.env.example` para `.env`.
2. Altere `JWT_SECRET` para uma chave longa e aleatória (mínimo 32 caracteres). Não versione `.env`.
3. Na raiz, execute:

   ```bash
   docker compose up --build
   ```

4. Abra `http://localhost:8080`.
5. Login demonstrativo: usuário `aluno`, senha `Agro@123`. Troque antes de qualquer exposição pública.
6. O simulador começa a publicar leituras automaticamente. Para gerar leituras extras, execute `docker compose restart sensor-simulator`.

Serviços:

| Serviço | Endereço |
|---|---|
| Dashboard/API | http://localhost:8080 |
| API de saúde | http://localhost:8080/actuator/health |
| MQTT TCP | localhost:1883 |
| MongoDB | localhost:27017 |
| Processing interno | http://localhost:8082 |

## Endpoints

Autenticação:

- `POST /api/auth/token` com `{"username":"aluno","password":"Agro@123"}`

Endpoints protegidos (header `Authorization: Bearer <token>`):

- `GET /api/sensors`
- `GET /api/readings?limit=100`
- `GET /api/alerts?limit=100`
- `GET /api/dashboard`

Endpoint interno de ingestão (rede Docker; não expor publicamente):

- `POST http://processing-service:8082/internal/readings`

Exemplo de leitura MQTT:

```json
{
  "sensorId":"sensor-001",
  "farmId":"fazenda-demo",
  "type":"SOIL_MOISTURE",
  "value":18.4,
  "unit":"%",
  "timestamp":"2026-11-03T12:00:00Z",
  "edgeStatus":"CRITICAL",
  "edgeAlert":"Umidade abaixo do limite configurado"
}
```

Tópico: `agro/{sensorId}/readings`.

## Testes e desempenho

O `processing-service` disponibiliza `POST /internal/benchmark?items=100000&threads=4`. Ele executa o mesmo cálculo sintético sequencialmente e com pool de threads e retorna tempos e speedup. Execute apenas em ambiente de teste. O benchmark é CPU-sintético e não deve ser confundido com latência real MQTT/API.

Para latência/throughput de ponta a ponta, gere uma carga controlada e registre quantidade de mensagens, duração, sucesso/erro e percentis de latência. Não invente resultados no relatório; inclua ambiente, quantidade de amostras e medições efetivamente obtidas.

## Segurança: limites do protótipo

- JWT protege os endpoints de consulta da API.
- Segredos são lidos por variáveis de ambiente.
- Mosquitto está sem autenticação/TLS apenas para execução local isolada. Para deploy, configure usuário/senha, ACL e TLS no broker.
- O endpoint `/internal/**` do processing deve permanecer privado na rede interna. Adicione autenticação serviço-a-serviço antes de expor qualquer serviço.
- HTTPS precisa ser terminado por proxy reverso/ingress com certificado (ex.: Caddy/Nginx/serviço cloud). O Compose local usa HTTP.
- A senha do usuário de demonstração é apenas para apresentação local.

## Deploy em nuvem

Use uma plataforma que suporte containers (AWS, GCP, Azure ou equivalente), MongoDB Atlas e broker MQTT gerenciado. Configure as variáveis do `.env.example` no gerenciador de segredos da plataforma, habilite TLS, restrinja o MongoDB por IP/rede privada e publique somente `api-service` atrás de HTTPS. Não publique portas 27017, 1883 ou 8082 na internet.

## Estrutura

```text
sensor-simulator/       simulador Java + Edge + MQTT
ingestion-service/      consumidor MQTT e encaminhamento HTTP
processing-service/     persistência, alertas e benchmark
api-service/            REST, JWT e dashboard
infra/mosquitto/         configuração local do broker
 docs/                   arquitetura e plano de testes
```

## Padrões de projeto demonstrados

- **Factory:** `SensorReadingFactory` valida/constrói leituras por tipo no simulador.
- **Observer:** `ReadingObserver`/`ConsoleAlertObserver` notifica quando Edge identifica leitura crítica.
- **Edge Computing:** regra simples de alerta executada no simulador antes da publicação.

## Próximos incrementos recomendados

1. Criar cadastro de fazendas/talhões e autorização por usuário.
2. Persistir trilha de auditoria e idempotência por `readingId`.
3. Configurar autenticação MQTT, ACL e TLS.
4. Criar testes de integração com broker e MongoDB reais.
5. Ajustar faixas de sensores de acordo com a cultura agrícola escolhida e fontes agronômicas.


## Esquema do banco de dados

O esquema do MongoDB está em `database/schema.mongodb.js`. Com os containers iniciados, execute o script a partir da raiz do projeto:

```powershell
Get-Content .\database\schema.mongodb.js | docker compose exec -T mongo mongosh agromonitor
```

O script cria (ou atualiza a validação de) as coleções `readings` e `alerts`, além dos índices usados nas consultas do painel.
